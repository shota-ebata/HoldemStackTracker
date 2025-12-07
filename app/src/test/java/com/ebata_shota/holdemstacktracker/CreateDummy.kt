package com.ebata_shota.holdemstacktracker

import com.ebata_shota.holdemstacktracker.domain.model.Game
import com.ebata_shota.holdemstacktracker.domain.model.GameId
import com.ebata_shota.holdemstacktracker.domain.model.GamePlayer
import com.ebata_shota.holdemstacktracker.domain.model.Phase
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import java.time.Instant

fun createDummyGame(
    players: List<GamePlayer> = emptyList(),
    phaseList: List<Phase> = emptyList()
) = Game(
    gameId = GameId(""),
    version = 0,
    tableId = TableId("0L"),
    appVersion = BuildConfig.VERSION_CODE.toLong(),
    btnPlayerId = PlayerId(""),
    players = players,
    potList = emptyList(),
    phaseList = phaseList,
    updateTime = Instant.now()
)

fun createDummyTable(
    playerOrder: List<PlayerId> = emptyList()
) = Table(
    id = TableId("0L"),
    version = 0L,
    hostAppVersionCode = BuildConfig.VERSION_CODE,
    hostPlayerId = PlayerId(""),
    potManagerPlayerId = PlayerId(""),
    rule = Rule.RingGame(sbSize = 100, bbSize = 200, defaultStack = 1000),
    playerOrder = playerOrder,
    btnPlayerId = PlayerId(""),
    currentGameId = GameId(""),
    basePlayers = emptyList(),
    waitPlayerIds = emptyMap(),
    tableStatus = TableStatus.PREPARING,
    startTime = null,
    tableCreateTime = Instant.now(),
    updateTime = Instant.now()
)