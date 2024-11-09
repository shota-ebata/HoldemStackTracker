package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.domain.model.GameState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableStateMapper
@Inject
constructor() {
    fun toModel(): GameState {
        // TODO
        return GameState(
            version = 0,
            players = emptyList(),
            podStateList = emptyList(),
            phaseStateList = emptyList(),
            timestamp = 0L,
        )
    }
}