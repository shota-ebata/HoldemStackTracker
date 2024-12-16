package com.ebata_shota.holdemstacktracker

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import java.time.Instant

fun createDummyGame(
    players: Set<GamePlayer> = emptySet(),
    phaseList: List<Phase> = emptyList()
) = Game(
    version = 0,
    appVersion = BuildConfig.VERSION_CODE.toLong(),
    players = players,
    podList = emptyList(),
    phaseList = phaseList,
    updateTime = Instant.now()
)

fun createDummyTable(
    playerOrder: List<PlayerId> = emptyList(),
    phaseList: List<Phase> = emptyList()
) = Table(
    id = TableId("0L"),
    version = 0L,
    appVersion = BuildConfig.VERSION_CODE.toLong(),
    hostPlayerId = PlayerId(""),
    rule = Rule.RingGame(sbSize = 100.0, bbSize = 200.0, betViewMode = BetViewMode.Number, defaultStack = 1000.0),
    playerOrder = playerOrder,
    btnPlayerId = PlayerId(""),
    basePlayers = emptyList(),
    waitPlayerIds = emptyList(),
    tableStatus = TableStatus.PREPARING,
    startTime = null,
    tableCreateTime = Instant.now(),
    updateTime = Instant.now()
)