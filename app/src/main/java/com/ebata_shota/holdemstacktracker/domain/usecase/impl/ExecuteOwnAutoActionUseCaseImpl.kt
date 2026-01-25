package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.AutoCheckOrFoldType
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.usecase.ExecuteCheckUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.ExecuteFoldUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.ExecuteOwnAutoActionUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsCurrentPlayerUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.IsEnableCheckUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class ExecuteOwnAutoActionUseCaseImpl @Inject constructor(
    private val isCurrentPlayer: IsCurrentPlayerUseCase,
    private val isEnableCheck: IsEnableCheckUseCase,
    private val doFold: ExecuteFoldUseCase,
    private val doCheck: ExecuteCheckUseCase,
) : ExecuteOwnAutoActionUseCase {
    override suspend fun invoke(
        table: Table,
        game: Game,
        myPlayerId: PlayerId,
        autoCheckOrFoldType: AutoCheckOrFoldType,
    ): Boolean {
        return if (
            isCurrentPlayer(game, myPlayerId) == true &&
            autoCheckOrFoldType is AutoCheckOrFoldType.ByGame &&
            autoCheckOrFoldType.gameId == game.gameId
        ) {
            // 自分の手番で
            // AutoCheck or AutoFold モードのときに
            // オートアクションを実行
            delay(1000L)
            val isEnableCheck = isEnableCheck(game, myPlayerId) ?: return false
            if (isEnableCheck) {
                doCheck(
                    currentGame = game,
                    rule = table.rule,
                    myPlayerId = myPlayerId,
                    leavedPlayerIds = table.leavedPlayerIds,
                )
            } else {
                doFold(
                    currentGame = game,
                    rule = table.rule,
                    myPlayerId = myPlayerId,
                    leavedPlayerIds = table.leavedPlayerIds,
                )
            }
            true
        } else {
            false
        }
    }
}