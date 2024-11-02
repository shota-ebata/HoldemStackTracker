package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.domain.model.TableState
import com.ebata_shota.holdemstacktracker.domain.repository.PrefRepository
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.infra.mapper.CurrentActionPlayerMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TableStateRepositoryImpl
@Inject
constructor(
    private val prefRepository: PrefRepository,
    private val currentActionPlayerMapper: CurrentActionPlayerMapper
) : TableStateRepository {
    override fun getTableStateFlow(tableId: Long): Flow<TableState> {
        TODO("Not yet implemented")
    }

    /**
     * TableStateを更新してFirebaseRealtimeDatabaseに送る
     *
     * @param newTableState 新しいTableState
     */
    override suspend fun setTableState(newTableState: TableState) {
        TODO("変換して送る")
    }
}