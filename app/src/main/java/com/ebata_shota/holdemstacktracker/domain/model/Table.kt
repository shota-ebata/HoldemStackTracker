package com.ebata_shota.holdemstacktracker.domain.model

data class Table(
    val id: TableId,
    val version: Long,
    val appVersion: Long,
    val hostPlayerId: PlayerId,
    val ruleState: RuleState,
    val basePlayers: List<PlayerBaseState>,
    val waitPlayers: List<PlayerBaseState>,
    val playerOrder: List<PlayerId>,
    val btnPlayerId: PlayerId,
    val tableStatus: TableStatus,
    val startTime: Long,
    val tableCreateTime: Long,
    val updateTime: Long
)