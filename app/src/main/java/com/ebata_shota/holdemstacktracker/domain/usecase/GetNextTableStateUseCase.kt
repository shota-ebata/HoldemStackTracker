package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.ActionState
import com.ebata_shota.holdemstacktracker.domain.model.TableState

interface GetNextTableStateUseCase {
    suspend fun invoke(
        latestTableState: TableState,
        action: ActionState
    ): TableState
}