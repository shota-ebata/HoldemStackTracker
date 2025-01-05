package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.Phase

interface GetLastPhaseAsBetPhaseUseCase {
    suspend fun invoke(
        phaseList: List<Phase>
    ): BetPhase
}