package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PhaseState.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.TableState

interface GetLatestBetPhaseUseCase {
    fun invoke(latestTableState: TableState): BetPhase
}