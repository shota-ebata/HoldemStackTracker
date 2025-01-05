package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.infra.model.BetPhaseActionType

interface GetActionTypeInLastPhaseAsBetPhaseUseCase {

    suspend fun invoke(
        phaseList: List<Phase>,
        playerId: PlayerId,
    ): BetPhaseActionType?
}