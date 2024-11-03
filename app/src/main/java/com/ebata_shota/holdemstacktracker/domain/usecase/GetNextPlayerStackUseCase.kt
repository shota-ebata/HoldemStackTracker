package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.BetPhaseActionState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerState
import com.ebata_shota.holdemstacktracker.domain.model.TableState

interface GetNextPlayerStackUseCase {
    suspend fun invoke(
        latestTableState: TableState,
        action: BetPhaseActionState
    ): List<PlayerState>
}