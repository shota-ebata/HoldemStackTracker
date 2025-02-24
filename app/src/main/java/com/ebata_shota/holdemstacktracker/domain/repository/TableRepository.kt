package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.PlayerId
import com.ebata_shota.holdemstacktracker.domain.model.Rule
import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
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

    suspend fun isExistsTable(tableId: TableId): Boolean

    suspend fun renameTableBasePlayer(
        tableId: TableId,
        indexOfBasePlayers: Long,
        playerId: PlayerId,
        name: String
    )

    suspend fun renameTableWaitPlayer(
        tableId: TableId,
        indexOfWaitPlayers: Long,
        playerId: PlayerId,
        name: String
    )
}