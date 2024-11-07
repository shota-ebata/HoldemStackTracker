package com.ebata_shota.holdemstacktracker.domain.usecase.impl

import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.usecase.GetLatestBetPhaseUseCase
import javax.inject.Inject

class GetLatestBetPhaseUseCaseImpl
@Inject constructor() : GetLatestBetPhaseUseCase {
    override fun invoke(latestGameState: GameState): BetPhase {
        val latestPhase: PhaseState? = latestGameState.phaseStateList.lastOrNull()
        if (latestPhase == null || latestPhase !is BetPhase) {
            throw IllegalStateException("BetPhase以外は想定外")
        }
        return latestPhase
    }
}