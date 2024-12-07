package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableSummary
import kotlinx.coroutines.flow.Flow

interface TableSummaryRepository {
    fun getTableSummaryListFlow(): Flow<List<TableSummary>>

    suspend fun saveTable(table: Table)
}