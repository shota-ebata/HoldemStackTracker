package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetPlayerLastActionInPhaseUseCase {

    suspend fun invoke(
        playerId: PlayerId,
        actionList: List<BetPhaseAction>,
    ): BetPhaseAction?
}