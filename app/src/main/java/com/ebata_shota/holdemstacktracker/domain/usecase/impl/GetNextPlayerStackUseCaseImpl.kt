package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherDefault
import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLastPhaseAsBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextGamePlayerStateListUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPlayerStackUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetNextPlayerStackUseCaseImpl
@Inject
constructor(
    private val getLastPhaseAsBetPhase: GetLastPhaseAsBetPhaseUseCase,
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    private val getNextPlayerStateList: GetNextGamePlayerStateListUseCase,
    @CoroutineDispatcherDefault
    private val dispatcher: CoroutineDispatcher
) : GetNextPlayerStackUseCase {

    override suspend fun invoke(
        latestGame: Game,
        action: BetPhaseAction,
    ): List<GamePlayer> = withContext(dispatcher) {
        // BetPhaseでしかActionはできないので
        val latestPhase: BetPhase = getLastPhaseAsBetPhase.invoke(latestGame.phaseList)
        // プレイヤーごとの、まだポットに入っていないベット額
        val pendingBetPerPlayer: Map<PlayerId, Int> = getPendingBetPerPlayer.invoke(
            playerOrder = latestGame.playerOrder,
            actionStateList = latestPhase.actionStateList
        )
        // プレイヤーのスタック更新
        val updatedPlayers: List<GamePlayer> = getNextPlayerStateList.invoke(
            pendingBetPerPlayer = pendingBetPerPlayer,
            players = latestGame.players,
            action = action
        )
        return@withContext updatedPlayers
    }
}