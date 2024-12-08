package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableSummary
import com.ebata_shota.holdemstacktracker.domain.repository.TableSummaryRepository
import com.ebata_shota.holdemstacktracker.infra.db.dao.TableSummaryDao
import com.ebata_shota.holdemstacktracker.infra.db.entity.TableSummaryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class TableSummaryRepositoryImpl
@Inject
constructor(
    private val tableSummaryDao: TableSummaryDao
) : TableSummaryRepository {

    override fun getTableSummaryListFlow(): Flow<List<TableSummary>> {
        return tableSummaryDao.getAllFlow().map { entityList ->
            entityList.map { entity ->
                TableSummary(
                    tableId = TableId(entity.tableId),
                    updateTime = entity.updateTime,
                    createTime = entity.createTime
                )
            }
        }
    }

    override suspend fun saveTable(table: Table) {
        val entity = TableSummaryEntity(
            tableId = table.id.value,
            updateTime = table.updateTime,
            createTime = table.tableCreateTime
        )
        tableSummaryDao.insert(entity)
    }
}