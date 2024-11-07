package com.ebata_shota.holdemstacktracker.domain.model

import java.time.LocalDateTime

data class GameState(
    val id: Long, // 固定
    val version: Long,
    val name: String, // 固定
    val hostPlayerId: PlayerId, // 固定
    val players: List<PlayerState>, // 固定
    val podStateList: List<PodState>,
    val playerOrder: List<PlayerId>, // 固定
    val btnPlayerId: PlayerId, // 固定
    val currentActionPlayer: PlayerId,
    val phaseStateList: List<PhaseState>,
    val ruleStatus: RuleState, // 固定
    val startTime: LocalDateTime // 固定
)
