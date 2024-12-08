package com.ebata_shota.holdemstacktracker

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayerState
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.PhaseState
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import java.time.Instant

fun createDummyGame(
    players: List<GamePlayerState> = emptyList(),
    phaseStateList: List<PhaseState> = emptyList()
) = Game(
    version = 0,
    appVersion = BuildConfig.VERSION_CODE.toLong(),
    players = players,
    podStateList = emptyList(),
    phaseStateList = phaseStateList,
    updateTime = Instant.now()
)

fun createDummyTable(
    playerOrder: List<PlayerId> = emptyList(),
    phaseStateList: List<PhaseState> = emptyList()
) = Table(
    id = TableId("0L"),
    version = 0L,
    appVersion = BuildConfig.VERSION_CODE.toLong(),
    hostPlayerId = PlayerId(""),
    ruleState = RuleState.RingGame(sbSize = 100.0, bbSize = 200.0, betViewMode = BetViewMode.Number, defaultStack = 1000.0),
    playerOrder = playerOrder,
    btnPlayerId = PlayerId(""),
    basePlayers = emptyList(),
    waitPlayers = emptyList(),
    tableStatus = TableStatus.STANDBY,
    startTime = null,
    tableCreateTime = Instant.now(),
    updateTime = Instant.now()
)