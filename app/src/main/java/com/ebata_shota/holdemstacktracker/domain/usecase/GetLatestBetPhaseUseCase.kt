package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.Phase.BetPhase
import com.ebata_shota.holdemstacktracker.domain.model.Game

interface GetLatestBetPhaseUseCase {
    fun invoke(latestGame: Game): BetPhase
}