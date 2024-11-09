package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.TableState
import kotlinx.coroutines.flow.Flow

interface TableStateRepository {
    val gameTableFlow: Flow<TableState>

    suspend fun setTableState(newTableState: TableState)
}