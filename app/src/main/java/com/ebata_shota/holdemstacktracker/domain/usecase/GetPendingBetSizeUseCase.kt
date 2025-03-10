package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetPendingBetSizeUseCase {
    suspend fun invoke(
        actionList: List<BetPhaseAction>,
        playerOrder: List<PlayerId>,
        playerId: PlayerId,
    ): Int
}