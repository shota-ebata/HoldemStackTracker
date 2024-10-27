package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.domain.model.TableState
import com.ebata_shota.holdemstacktracker.domain.repository.TableStateRepository
import com.ebata_shota.holdemstacktracker.infra.mapper.CurrentActionPlayerMapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TableStateRepositoryImpl
@Inject
constructor(
    private val currentActionPlayerMapper: CurrentActionPlayerMapper
) : TableStateRepository {
    override fun getTableStateFlow(tableId: Long): Flow<TableState> {
        TODO("Not yet implemented")
    }

    override suspend fun setTableState(tableState: TableState) {
        TODO("Not yet implemented")
    }
}