package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerState

interface GetNextPlayerStateListUseCase {
    suspend fun invoke(
        pendingBetPerPlayer: Map<PlayerId, Float>,
        players: List<PlayerState>,
        action: BetPhaseActionState
    ): List<PlayerState>
}