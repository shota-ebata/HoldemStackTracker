package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.di.annotation.CoroutineDispatcherIO
import com.ebata_shota.holdemstacktracker.domain.model.PhaseHistory
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.repository.PhaseHistoryRepository
import com.ebata_shota.holdemstacktracker.infra.db.dao.PhaseHistoryDao
import com.ebata_shota.holdemstacktracker.infra.db.entity.PhaseHistoryEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhaseHistoryRepositoryImpl
@Inject
constructor(
    private val dao: PhaseHistoryDao,
    @CoroutineDispatcherIO
    private val dispatcher: CoroutineDispatcher,
) : PhaseHistoryRepository {

    override suspend fun getPhaseHistory(
        tableId: TableId,
        phaseId: PhaseId,
    ): PhaseHistory? = withContext(dispatcher) {
        val entity = dao.findById(
            tableIdString = tableId.value,
            phaseIdString = phaseId.value
        )
        return@withContext entity?.let {
            PhaseHistory(
                tableId = TableId(entity.tableId),
                phaseId = PhaseId(entity.phaseId),
                isFinished = entity.isFinished,
                timestamp = entity.timestamp,
            )
        }
    }

    override suspend fun savePhaseHistory(phaseHistory: PhaseHistory) {
        dao.insert(
            PhaseHistoryEntity(
                tableId = phaseHistory.tableId.value,
                phaseId = phaseHistory.phaseId.value,
                isFinished = phaseHistory.isFinished,
                timestamp = phaseHistory.timestamp,
            )
        )
    }

    override suspend fun saveFinishPhase(
        tableId: TableId,
        phaseId: PhaseId,
    ) = withContext(dispatcher) {
        val entity = dao.findById(
            tableIdString = tableId.value,
            phaseIdString = phaseId.value
        )
        if (entity != null) {
            dao.insert(
                entity.copy(isFinished = true)
            )
        }
    }
}
