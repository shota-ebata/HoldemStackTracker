package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.TableState
import kotlinx.coroutines.flow.Flow

interface TableStateRepository {
    fun getTableStateFlow(tableId: Long): Flow<TableState>
    suspend fun setTableState(newTableState: TableState)
}