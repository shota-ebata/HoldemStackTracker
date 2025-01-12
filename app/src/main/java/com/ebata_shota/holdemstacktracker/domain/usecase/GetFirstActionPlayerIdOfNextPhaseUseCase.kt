package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetFirstActionPlayerIdOfNextPhaseUseCase {
    suspend fun invoke(
        btnPlayerId: PlayerId,
        playerOrder: List<PlayerId>,
        currentGame: Game,
    ): PlayerId?
}