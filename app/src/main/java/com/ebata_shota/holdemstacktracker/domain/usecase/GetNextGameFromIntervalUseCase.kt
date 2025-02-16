package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game

interface GetNextGameFromIntervalUseCase {

    suspend fun invoke(
        currentGame: Game,
    ): Game
}