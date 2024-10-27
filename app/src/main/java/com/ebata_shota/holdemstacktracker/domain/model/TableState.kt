package com.ebata_shota.holdemstacktracker.domain.model

import java.time.LocalDateTime

data class TableState(
    val id: Long,
    val version: Int,
    val name: String,
    val hostPlayerId: PlayerId,
    val players: List<PlayerState>,
    val podInfoList: List<PodState>,
    val playerOrder: List<PlayerId>,
    val btnPlayerId: PlayerId,
    val currentActionPlayer: PlayerId,
    val phaseStateList: List<PhaseState>,
    val ruleStatus: RuleState,
    val startTime: LocalDateTime
)
