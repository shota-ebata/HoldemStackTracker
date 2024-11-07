package com.ebata_shota.holdemstacktracker

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.GameState
import java.time.LocalDateTime

fun createDummyGameState(
    playerOrder: List<PlayerId> = emptyList(),
    phaseStateList: List<PhaseState> = emptyList()
) = GameState(
    id = 0L,
    version = 0,
    name = "dummy",
    hostPlayerId = PlayerId(""),
    players = emptyList(),
    podStateList = emptyList(),
    playerOrder = playerOrder,
    btnPlayerId = PlayerId(""),
    currentActionPlayer = PlayerId(""),
    phaseStateList = phaseStateList,
    ruleStatus = RuleState.LingGame(sbSize = 100.0, bbSize = 200.0, BetViewMode.Number),
    startTime = LocalDateTime.now()
)