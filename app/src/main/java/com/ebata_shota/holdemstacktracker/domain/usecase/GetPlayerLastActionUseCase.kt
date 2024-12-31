package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseAction
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId

interface GetPlayerLastActionUseCase {

    suspend fun invoke(
        playerId: PlayerId,
        phaseList: List<Phase>
    ): BetPhaseAction?
}