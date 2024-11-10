package com.ebata_shota.holdemstacktracker.domain.model

/**
 * ゲーム中に頻繁更新が入る情報
 */
data class GameState(
    val version: Long,
    val appVersion: Long,
    val players: List<GamePlayerState>,
    val podStateList: List<PodState>,
    val phaseStateList: List<PhaseState>,
    val timestamp: Long
) {
    val playerOrder: List<PlayerId>
        get() = players.map { it.id }
}
