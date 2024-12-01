package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.Game

interface GetNextGameUseCase {
    suspend fun invoke(
        latestGame: Game,
        action: ActionState
    ): Game
}