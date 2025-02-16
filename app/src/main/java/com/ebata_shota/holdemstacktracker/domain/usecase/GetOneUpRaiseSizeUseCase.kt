package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetOneUpRaiseSizeUseCase {
    suspend fun invoke(
        currentRaiseSize: Int,
        game: Game,
    ): Int
}