package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

fun interface IsCurrentPlayerUseCase {
    suspend operator fun invoke(
        game: Game,
        playerId: PlayerId,
    ): Boolean?
}
