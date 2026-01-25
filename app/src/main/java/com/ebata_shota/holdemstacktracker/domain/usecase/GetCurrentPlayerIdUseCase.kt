package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

fun interface GetCurrentPlayerIdUseCase {
    suspend operator fun invoke(
        btnPlayerId: PlayerId,
        playerOrder: List<PlayerId>,
        currentBetPhase: Phase.BetPhase,
    ): PlayerId?
}
