package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.model.GameState

interface GetNextPlayerStackUseCase {
    suspend fun invoke(
        latestGameState: GameState,
        action: BetPhaseActionState
    ): List<GamePlayerState>
}