package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

fun interface GetPendingBetSizeUseCase {
    suspend operator fun invoke(
        actionList: List<BetPhaseAction>,
        playerOrder: List<PlayerId>,
        playerId: PlayerId,
    ): Int
}
