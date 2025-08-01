package com.ebata_shota.holdemstacktracker.domain.model

import java.time.Instant

data class Table(
    val id: TableId,
    val version: Long,
    val hostAppVersionCode: Int,
    val hostPlayerId: PlayerId,
    val potManagerPlayerId: PlayerId,
    val rule: Rule,
    val basePlayers: List<PlayerBase>,
    val waitPlayerIds: List<PlayerId>,
    val playerOrder: List<PlayerId>,
    val btnPlayerId: PlayerId,
    val tableStatus: TableStatus,
    val currentGameId: GameId?,
    val startTime: Instant?,
    val tableCreateTime: Instant,
    val updateTime: Instant
) {
    /**
     * 退席しているプレイヤーを除いたプレイヤー順リスト
     */
    val playerOrderWithoutLeaved: List<PlayerId> = playerOrder.filter {
        basePlayers.find { playerBase -> playerBase.id == it }?.isLeaved == false
    }

    /**
     * 堆積しているプレイヤーを除いたBasePlayerリスト
     */
    val basePlayersWithoutLeaved: List<PlayerBase> = basePlayers.filter {
        !it.isLeaved && playerOrder.contains(it.id)
    }
}