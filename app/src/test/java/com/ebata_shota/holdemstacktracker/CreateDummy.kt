package com.ebata_shota.holdemstacktracker

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import java.time.LocalDateTime

fun createDummyTableState(
    phaseStateList: List<PhaseState>
) = TableState(
    id = 0L,
    version = 0,
    name = "dummy",
    hostPlayerId = PlayerId(""),
    players = emptyList(),
    podStateList = emptyList(),
    playerOrder = emptyList(),
    btnPlayerId = PlayerId(""),
    currentActionPlayer = PlayerId(""),
    phaseStateList = phaseStateList,
    ruleStatus = RuleState.LingGame(sbSize = 100.0f, bbSize = 200.0f, BetViewMode.Number),
    startTime = LocalDateTime.now()
)