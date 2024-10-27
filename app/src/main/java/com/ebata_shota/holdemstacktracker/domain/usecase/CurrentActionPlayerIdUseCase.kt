package com.ebata_shota.holdemstacktracker.domain.usecase

import com.ebata_shota.holdemstacktracker.domain.model.PhaseState

interface CurrentActionPlayerIdUseCase {
    fun getCurrentActionPlayerId(
        playerOrder: List<Long>,
        basePlayerId: Long,
        phaseStateList: List<PhaseState>
    ): Long?
}