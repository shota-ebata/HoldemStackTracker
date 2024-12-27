package com.ebata_shota.holdemstacktracker.domain.model

import java.time.Instant

/**
 * ゲーム中に頻繁更新が入る情報
 */
data class Game(
    val version: Long,
    val appVersion: Long,
    val players: Set<GamePlayer>,
    val potList: List<Pot>,
    val phaseList: List<Phase>,
    val updateTime: Instant
)
