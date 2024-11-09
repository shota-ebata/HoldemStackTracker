package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetCurrentPlayerIdUseCase {
    suspend fun invoke(
        btnPlayerId: PlayerId,
        gameState: GameState
    ): PlayerId
}
