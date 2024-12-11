package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Action
import com.ebata_shota.holdemstacktracker.domain.model.Game

interface GetNextGameUseCase {
    suspend fun invoke(
        latestGame: Game,
        action: Action
    ): Game
}