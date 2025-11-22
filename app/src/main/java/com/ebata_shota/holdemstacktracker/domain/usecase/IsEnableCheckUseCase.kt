package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface IsEnableCheckUseCase {
    suspend fun invoke(
        game: Game,
        myPlayerId: PlayerId,
    ): Boolean?
}