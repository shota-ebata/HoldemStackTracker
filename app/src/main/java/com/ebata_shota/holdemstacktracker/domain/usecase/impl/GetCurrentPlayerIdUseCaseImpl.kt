package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import javax.inject.Inject

class GetCurrentPlayerIdUseCaseImpl
@Inject constructor(
    private val getLatestBetPhase: GetLatestBetPhaseUseCase
) : GetCurrentPlayerIdUseCase {

    /**
     * 現在のゲーム状態[GameState]で
     * プレイするべきプレイヤーを返す。
     */
    override suspend fun invoke(
        btnPlayerId: PlayerId,
        gameState: GameState
    ): PlayerId {
        // 全員の最後のアクションを一つづつ取得
        val latestBetPhase = getLatestBetPhase.invoke(gameState)
        // 全員の最後のアクションを一つづつ取得
        val actionStateList = latestBetPhase.actionStateList
        val latestActionPlayerId = actionStateList.lastOrNull()?.playerId ?: btnPlayerId
        val latestActionPlayerIndex = gameState.playerOrder.indexOf(latestActionPlayerId)
        val nextPlayerIndex = (latestActionPlayerIndex + 1) % gameState.playerOrder.size
        return gameState.playerOrder[nextPlayerIndex]
    }
}