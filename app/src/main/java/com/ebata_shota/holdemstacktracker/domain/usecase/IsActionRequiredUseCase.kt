package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.TableState

interface IsActionRequiredUseCase {
    fun invoke(
        latestTableState: TableState
    ): Boolean
}