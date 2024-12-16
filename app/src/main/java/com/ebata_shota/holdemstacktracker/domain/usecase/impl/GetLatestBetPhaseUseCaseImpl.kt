package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import javax.inject.Inject

class GetLatestBetPhaseUseCaseImpl
@Inject constructor() : GetLatestBetPhaseUseCase {

    override fun invoke(latestGame: Game): BetPhase {
        val latestPhase: Phase? = latestGame.phaseList.lastOrNull()
        if (latestPhase == null || latestPhase !is BetPhase) {
            throw IllegalStateException("BetPhase以外は想定外")
        }
        return latestPhase
    }
}