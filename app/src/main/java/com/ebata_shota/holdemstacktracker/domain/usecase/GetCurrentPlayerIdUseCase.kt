package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetCurrentPlayerIdUseCase {
    suspend fun invoke(
        btnPlayerId: PlayerId,
        playerOrder: List<PlayerId>,
        phaseList: List<Phase>
    ): PlayerId
}
