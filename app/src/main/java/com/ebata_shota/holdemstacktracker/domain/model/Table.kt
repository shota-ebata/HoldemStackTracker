package com.ebata_shota.holdemstacktracker.domain.model

import java.time.Instant

data class Table(
    val id: TableId,
    val version: Long,
    val appVersion: Long,
    val hostPlayerId: PlayerId,
    val rule: Rule,
    val basePlayers: List<PlayerBaseState>,
    val waitPlayers: List<PlayerBaseState>,
    val playerOrder: List<PlayerId>,
    val btnPlayerId: PlayerId,
    val tableStatus: TableStatus,
    val startTime: Instant?,
    val tableCreateTime: Instant,
    val updateTime: Instant
)