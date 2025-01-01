package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherIO
import com.ebata_shota.holdemstacktracker.domain.model.ActionHistory
import com.ebata_shota.holdemstacktracker.domain.model.ActionId
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.ActionHistoryRepository
import com.ebata_shota.holdemstacktracker.infra.db.dao.ActionHistoryDao
import com.ebata_shota.holdemstacktracker.infra.db.entity.ActionHistoryEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ActionHistoryRepositoryImpl
@Inject
constructor(
    private val dao: ActionHistoryDao,
    @CoroutineDispatcherIO
    private val dispatcher: CoroutineDispatcher,
) : ActionHistoryRepository {

    override suspend fun getActionHistory(
        tableId: TableId,
        actionId: ActionId,
    ): ActionHistory? = withContext(dispatcher) {
        val entity = dao.findById(
            tableIdString = tableId.value,
            actionIdString = actionId.value
        )
        return@withContext entity?.let {
            ActionHistory(
                tableId = TableId(entity.tableId),
                actionId = ActionId(entity.actionId),
                hadSeen = entity.hadSeen,
                timestamp = entity.timestamp
            )
        }
    }

    override suspend fun saveActionHistory(actionHistory: ActionHistory) = withContext(dispatcher) {
        dao.insert(
            ActionHistoryEntity(
                tableId = actionHistory.tableId.value,
                actionId = actionHistory.actionId.value,
                hadSeen = actionHistory.hadSeen,
                timestamp = actionHistory.timestamp
            )
        )
    }

    override suspend fun sawAction(
        tableId: TableId,
        actionId: ActionId,
    ) {
        val actionHistory = dao.findById(
            tableIdString = tableId.value,
            actionIdString = actionId.value
        )
        if (actionHistory != null) {
            dao.insert(actionHistory.copy(hadSeen = true))
        }
    }

}