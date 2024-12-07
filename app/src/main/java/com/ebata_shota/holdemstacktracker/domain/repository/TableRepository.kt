package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.RuleState
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.Table
import kotlinx.coroutines.flow.SharedFlow

interface TableRepository {
    val tableFlow: SharedFlow<Table>

    suspend fun createNewTable(
        tableId: TableId,
        ruleState: RuleState
    )

    fun startCollectTableFlow(tableId: TableId)
    fun stopCollectTableFlow()
    // FIXME: updateTimeとかversionの更新処理を行うUseCaseを用意したほうがいいね
    suspend fun sendTable(table: Table)
}