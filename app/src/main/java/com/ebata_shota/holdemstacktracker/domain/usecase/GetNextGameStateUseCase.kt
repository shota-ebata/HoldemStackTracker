package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.GameState

interface GetNextGameStateUseCase {
    suspend fun invoke(
        latestGameState: GameState,
        action: ActionState
    ): GameState
}