package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.Phase

fun interface GetLastPhaseAsBetPhaseUseCase {
    suspend operator fun invoke(
        phaseList: List<Phase>
    ): BetPhase
}
