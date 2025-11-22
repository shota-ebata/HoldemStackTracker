package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface IsCurrentPlayerUseCase {
    suspend fun invoke(
        game: Game,
        playerId: PlayerId,
    ): Boolean?
}