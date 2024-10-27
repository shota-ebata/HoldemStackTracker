package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.TableState

interface CurrentActionPlayerIdUseCase {
    fun getCurrentActionPlayerId(tableState: TableState): Long?
}