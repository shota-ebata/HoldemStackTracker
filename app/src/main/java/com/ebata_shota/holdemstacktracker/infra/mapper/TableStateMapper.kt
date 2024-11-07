package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
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
            currentActionPlayer = PlayerId(""),
            phaseStateList = emptyList(),
            timestamp = 0L,
        )
    }
}