package com.ebata_shota.holdemstacktracker.domain.model

import java.time.LocalDateTime

data class TableState(
    val id: TableId,
    val version: Long,
    val name: String,
    val hostPlayerId: PlayerId,
    val ruleStatus: RuleState,
    val basePlayers: List<PlayerBaseState>,
    val waitPlayers: List<PlayerBaseState>,
    val playerOrder: List<PlayerId>,
    val btnPlayerId: PlayerId,
    val startTime: LocalDateTime
)