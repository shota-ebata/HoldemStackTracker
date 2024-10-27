package com.ebata_shota.holdemstacktracker.domain.model

import java.time.LocalDateTime

data class TableState(
    val id: Long,
    val version: Int,
    val name: String,
    val hostPlayerId: Long,
    val players: List<PlayerState>,
    val podInfoList: List<PodState>,
    val playerOrder: List<Long>,
    val btnPlayerId: Long,
    val currentActionPlayer: Long,
    val phaseStateList: List<PhaseState>,
    val ruleStatus: RuleState,
    val startTime: LocalDateTime
)
