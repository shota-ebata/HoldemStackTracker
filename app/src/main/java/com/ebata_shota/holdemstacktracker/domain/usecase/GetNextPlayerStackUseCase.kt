package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.model.Game

interface GetNextPlayerStackUseCase {
    suspend fun invoke(
        latestGame: Game,
        action: BetPhaseActionState
    ): List<GamePlayerState>
}