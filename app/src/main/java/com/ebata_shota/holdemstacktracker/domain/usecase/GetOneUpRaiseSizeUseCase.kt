package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game

fun interface GetOneUpRaiseSizeUseCase {
    suspend operator fun invoke(
        currentRaiseSize: Int,
        game: Game,
    ): Int
}
