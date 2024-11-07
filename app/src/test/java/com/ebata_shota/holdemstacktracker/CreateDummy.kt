package com.ebata_shota.holdemstacktracker

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import java.time.LocalDateTime

fun createDummyGameState(
    phaseStateList: List<PhaseState> = emptyList()
) = GameState(
    version = 0,
    players = emptyList(),
    podStateList = emptyList(),
    currentActionPlayer = PlayerId(""),
    phaseStateList = phaseStateList,
    timestamp = 0L
)

fun createDummyTableState(
    playerOrder: List<PlayerId> = emptyList(),
    phaseStateList: List<PhaseState> = emptyList()
) = TableState(
    id = TableId("0L"),
    version = 0L,
    name = "dummy",
    hostPlayerId = PlayerId(""),
    ruleStatus = RuleState.LingGame(sbSize = 100.0, bbSize = 200.0, BetViewMode.Number),
    playerOrder = playerOrder,
    btnPlayerId = PlayerId(""),
    basePlayers = emptyList(),
    waitPlayers = emptyList(),
    startTime = LocalDateTime.now()
)