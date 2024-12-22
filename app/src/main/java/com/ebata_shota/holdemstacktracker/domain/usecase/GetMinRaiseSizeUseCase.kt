package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game

interface GetMinRaiseSizeUseCase {
    fun invoke(
        game: Game,
        minBetSize: Double
    ): Double
}