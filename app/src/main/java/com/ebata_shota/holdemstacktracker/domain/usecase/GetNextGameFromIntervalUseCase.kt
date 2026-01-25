package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game

fun interface GetNextGameFromIntervalUseCase {

    suspend operator fun invoke(
        currentGame: Game,
    ): Game
}
