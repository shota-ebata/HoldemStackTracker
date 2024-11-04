package com.ebata_shota.holdemstacktracker.infra.mapper

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableStateMapper
@Inject
constructor() {
    fun toModel(): TableState {
        // TODO
        return TableState(
            id = 0L,
            version = 0,
            name = "dummy",
            hostPlayerId = PlayerId(""),
            players = emptyList(),
            podStateList = emptyList(),
            playerOrder = emptyList(),
            btnPlayerId = PlayerId(""),
            currentActionPlayer = PlayerId(""),
            phaseStateList = emptyList(),
            ruleStatus = RuleState.LingGame(sbSize = 100.0, bbSize = 200.0, BetViewMode.Number),
            startTime = LocalDateTime.now()
        )
    }
}