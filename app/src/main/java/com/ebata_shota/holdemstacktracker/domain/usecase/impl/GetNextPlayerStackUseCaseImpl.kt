package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerState
import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPlayerStackUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetNextPlayerStateListUseCase
import com.ebata_shota.holdemstacktracker.domain.usecase.GetPendingBetPerPlayerUseCase
import javax.inject.Inject

class GetNextPlayerStackUseCaseImpl
@Inject
constructor(
    private val getLatestBetPhase: GetLatestBetPhaseUseCase,
    private val getPendingBetPerPlayer: GetPendingBetPerPlayerUseCase,
    private val getNextPlayerStateList: GetNextPlayerStateListUseCase,
) : GetNextPlayerStackUseCase {
    override suspend fun invoke(
        latestGameState: GameState,
        action: BetPhaseActionState
    ): List<PlayerState> {
        // BetPhaseでしかActionはできないので
        val latestPhase: BetPhase = getLatestBetPhase.invoke(latestGameState)
        // プレイヤーごとの、まだポッドに入っていないベット額
        val pendingBetPerPlayer: Map<PlayerId, Double> = getPendingBetPerPlayer.invoke(
            playerOrder = latestGameState.playerOrder,
            actionStateList = latestPhase.actionStateList
        )
        // プレイヤーのスタック更新
        val updatedPlayers: List<PlayerState> = getNextPlayerStateList.invoke(
            pendingBetPerPlayer = pendingBetPerPlayer,
            players = latestGameState.players,
            action = action
        )
        return updatedPlayers
    }
}