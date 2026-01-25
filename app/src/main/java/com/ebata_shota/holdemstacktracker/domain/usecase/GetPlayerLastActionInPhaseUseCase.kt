package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

fun interface GetPlayerLastActionInPhaseUseCase {

    suspend operator fun invoke(
        playerId: PlayerId,
        actionList: List<BetPhaseAction>,
    ): BetPhaseAction?
}
