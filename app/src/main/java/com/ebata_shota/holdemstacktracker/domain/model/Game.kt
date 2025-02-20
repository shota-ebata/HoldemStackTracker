package com.ebata_shota.holdemstacktracker.domain.model

import java.time.Instant

/**
 * ゲーム中に頻繁更新が入る情報
 */
data class Game(
    val gameId: GameId,
    val version: Long,
    val tableId: TableId, // MEMO: RealtimeDBには保存はされない
    val appVersion: Long,
    val btnPlayerId: PlayerId,
    val players: List<GamePlayer>,
    val potList: List<Pot>,
    val phaseList: List<Phase>,
    val updateTime: Instant,
) {
    val playerOrder: List<PlayerId> = players.map { it.id }
}
