package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState

interface GetNextGamePlayerStateListUseCase {
    suspend fun invoke(
        pendingBetPerPlayer: Map<PlayerId, Double>,
        players: List<GamePlayerState>,
        action: BetPhaseActionState
    ): List<GamePlayerState>
}