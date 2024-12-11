package com.ebata_shota.holdemstacktracker.domain.model

import java.time.Instant

/**
 * ゲーム中に頻繁更新が入る情報
 */
data class Game(
    val version: Long,
    val appVersion: Long,
    val players: List<GamePlayerState>,
    val podList: List<Pod>,
    val phaseList: List<Phase>,
    val updateTime: Instant
) {
    val playerOrder: List<PlayerId>
        get() = players.map { it.id }
}
