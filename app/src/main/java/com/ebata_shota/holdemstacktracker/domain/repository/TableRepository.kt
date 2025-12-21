package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.GameId
import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus
import kotlinx.coroutines.flow.StateFlow

interface TableRepository {
    val tableStateFlow: StateFlow<Result<Table>?>

    val currentTableId: TableId?

    suspend fun createNewTable(
        tableId: TableId,
        rule: Rule
    )

    fun startCollectTableFlow(tableId: TableId)
    fun stopCollectTableFlow()
    // FIXME: updateTimeとかversionの更新処理を行うUseCaseを用意したほうがいいね
    suspend fun sendTable(table: Table)

    suspend fun addBasePlayer(
        tableId: TableId,
        playerId: PlayerId,
        name: String,
    )

    suspend fun addPlayerOrder(
        tableId: TableId,
        newPlayerOrder: List<PlayerId>,
        addPlayerIds: Map<String, PlayerId>,
    )

    suspend fun updateBasePlayer(
        tableId: TableId,
        playerId: PlayerId,
        newStack: Int? = null,
        newIsSeated: Boolean? = null,
    )

    suspend fun updateBasePlayerStacks(
        tableId: TableId,
        stacks: Map<PlayerId, Int>,
    )

    suspend fun updatePlayerOrder(
        tableId: TableId,
        playerOrder: List<PlayerId>,
    )

    suspend fun updateRule(
        tableId: TableId,
        rule: Rule,
    )

    suspend fun updateTableStatus(
        tableId: TableId,
        tableStatus: TableStatus,
        gameId: GameId? = null,
    )

    suspend fun addBanPlayers(
        tableId: TableId,
        newPlayerOrder: List<PlayerId>,
        banPlayerIds: List<PlayerId>,
    )

    suspend fun isExistsTable(tableId: TableId): Boolean

    suspend fun renameTableBasePlayer(
        tableId: TableId,
        indexOfBasePlayers: Long,
        playerId: PlayerId,
        name: String
    )

    fun startCurrentTableConnectionIfNeed(tableId: TableId)
    fun stopCurrentTableCurrentTableConnection(tableId: TableId)
}