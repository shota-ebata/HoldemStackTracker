package com.ebata_shota.holdemstacktracker.domain.model

import java.time.LocalDateTime

data class TableState(
    val tableId: Long,
    val version: Int,
    val name: String,
    val hostPlayerId: Long,
    val players: List<PlayerState>,
    val podInfoList: List<PodState>,
    val playerOrder: List<Long>,
    val phaseStatusList: List<PhaseState>,
    val ruleStatus: RuleState,
    val startTime: LocalDateTime
)


