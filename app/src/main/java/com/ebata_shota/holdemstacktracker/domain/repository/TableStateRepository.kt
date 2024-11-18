package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableState
import kotlinx.coroutines.flow.SharedFlow

interface TableStateRepository {
    val tableFlow: SharedFlow<TableState>

    suspend fun createNewTable(
        tableId: TableId,
        ruleState: RuleState
    )

    fun startCollectTableFlow(tableId: TableId)
    fun stopCollectTableFlow()
    suspend fun sendTableState(newTableState: TableState)
}