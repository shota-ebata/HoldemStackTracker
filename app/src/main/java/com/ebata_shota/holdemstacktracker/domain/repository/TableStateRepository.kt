package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import kotlinx.coroutines.flow.Flow

interface TableStateRepository {
    val tableStateFlow: Flow<TableState>

    suspend fun createNewTable(
        tableId: TableId,
        tableName: String,
        ruleState: RuleState
    )

    fun startCollectTableStateFlow(tableId: TableId)
    suspend fun setTableState(newTableState: TableState)
}