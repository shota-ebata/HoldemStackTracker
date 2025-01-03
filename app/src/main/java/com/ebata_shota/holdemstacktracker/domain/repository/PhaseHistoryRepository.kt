package com.ebata_shota.holdemstacktracker.domain.repository

import com.ebata_shota.holdemstacktracker.domain.model.PhaseHistory
import com.ebata_shota.holdemstacktracker.domain.model.PhaseId
import com.ebata_shota.holdemstacktracker.domain.model.TableId

interface PhaseHistoryRepository {

    suspend fun getPhaseHistory(
        tableId: TableId,
        phaseId: PhaseId,
    ): PhaseHistory?

    suspend fun savePhaseHistory(phaseHistory: PhaseHistory)

    suspend fun saveFinishPhase(
        tableId: TableId,
        phaseId: PhaseId,
    )
}
