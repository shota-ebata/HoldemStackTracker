package com.ebata_shota.holdemstacktracker.domain.model

data class TableState(
    val id: TableId,
    val version: Long,
    val appVersion: Long,
    val name: String,
    val hostPlayerId: PlayerId,
    val ruleState: RuleState,
    val basePlayers: List<PlayerBaseState>,
    val waitPlayers: List<PlayerBaseState>,
    val playerOrder: List<PlayerId>,
    val btnPlayerId: PlayerId,
    val startTime: Long
)