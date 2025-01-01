package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.ActionHistory
import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.TableId

interface ActionHistoryRepository {
    suspend fun getActionHistory(
        tableId: TableId,
        actionId: ActionId,
    ): ActionHistory?

    suspend fun saveActionHistory(actionHistory: ActionHistory)

    suspend fun sawAction(
        tableId: TableId,
        actionId: ActionId,
    )
}