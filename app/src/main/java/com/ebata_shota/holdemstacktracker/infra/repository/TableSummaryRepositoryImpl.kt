package com.ebata_shota.holdemstacktracker.infra.repository

import com.ebata_shota.holdemstacktracker.domain.model.Table
import com.ebata_shota.holdemstacktracker.domain.model.TableId
import com.ebata_shota.holdemstacktracker.domain.model.TableSummary
import com.ebata_shota.holdemstacktracker.domain.repository.TableSummaryRepository
import com.ebata_shota.holdemstacktracker.infra.db.dao.TableSummaryDao
import com.ebata_shota.holdemstacktracker.infra.db.entity.TableSummaryEntity
import com.ebata_shota.holdemstacktracker.infra.extension.blindText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
                    blindText = entity.blindText,
                    hostName = entity.hostName,
                    playerSize = entity.playerSize,
                    updateTime = entity.updateTime,
                    createTime = entity.createTime
                )
            }
        }
    }

    override suspend fun saveTable(table: Table) {
        val entity = TableSummaryEntity(
            tableId = table.id.value,
            blindText = table.rule.blindText(),
            hostName = table.hostPlayerId.let { hostPlayerId ->
                table.basePlayers.find { it.id == hostPlayerId }?.name.orEmpty()
            },
            playerSize = "${table.playerOrder.size}/10", // FIXME: 10人上限がハードコーディングされている
            updateTime = table.updateTime,
            createTime = table.tableCreateTime
        )
        tableSummaryDao.insert(entity)
    }
}