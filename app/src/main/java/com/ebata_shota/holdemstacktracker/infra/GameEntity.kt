package com.ebata_shota.holdemstacktracker.infra

import com.ebata_shota.holdemstacktracker.domain.model.PhaseState

data class GameEntity(
    val phaseStateList: List<PhaseState>
)