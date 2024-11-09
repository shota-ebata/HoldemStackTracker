package com.ebata_shota.holdemstacktracker

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.model.GameState
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableState

fun createDummyGameState(
    players: List<GamePlayerState> = emptyList(),
    phaseStateList: List<PhaseState> = emptyList()
) = GameState(
    version = 0,
    players = players,
    podStateList = emptyList(),
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
    ruleState = RuleState.LingGame(sbSize = 100.0, bbSize = 200.0, BetViewMode.Number),
    playerOrder = playerOrder,
    btnPlayerId = PlayerId(""),
    basePlayers = emptyList(),
    waitPlayers = emptyList(),
    startTime = 0L
)