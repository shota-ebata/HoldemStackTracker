package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

fun interface GetFirstActionPlayerIdOfNextPhaseUseCase {
    suspend operator fun invoke(
        currentGame: Game,
    ): PlayerId?
}
