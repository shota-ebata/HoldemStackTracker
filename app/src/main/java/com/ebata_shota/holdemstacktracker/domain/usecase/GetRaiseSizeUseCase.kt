package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

fun interface GetRaiseSizeUseCase {
    suspend operator fun invoke(
        game: Game,
        myPlayerId: PlayerId,
        minRaiseSize: Int,
        sliderPosition: Float,
    ): Int
}
