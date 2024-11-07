package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.GameState

interface GetLatestBetPhaseUseCase {
    fun invoke(latestGameState: GameState): BetPhase
}