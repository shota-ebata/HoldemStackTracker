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
    val waitPlayerIds: Map<String, PlayerId>,
    val playerOrder: List<PlayerId>,
    val btnPlayerId: PlayerId,
    val tableStatus: TableStatus,
    val currentGameId: GameId?,
    val startTime: Instant?,
    val tableCreateTime: Instant,
    val updateTime: Instant
) {
    /**
     * 離席しているプレイヤーを除いたプレイヤー順リスト
     */
    val playerOrderWithoutLeaved: List<PlayerId> = playerOrder.filter {
        basePlayers.find { playerBase -> playerBase.id == it }?.isSeated == true
    }

    /**
     * 離席しているプレイヤーを除いたBasePlayerリスト
     */
    val basePlayersWithoutLeaved: List<PlayerBase> = basePlayers.filter {
        it.isSeated && playerOrder.contains(it.id)
    }

    fun getPlayerName(playerId: PlayerId): String? {
        return basePlayers.find { it.id == playerId }?.name
    }

    fun getPlayerNameMap(): Map<PlayerId, String> = basePlayers.associate { it.id to it.name }
}