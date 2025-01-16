package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetGameInAdvancedPhaseUseCase {

    suspend fun invoke(
        playerOrder: List<PlayerId>,
        currentGame: Game,
    ): Game
}