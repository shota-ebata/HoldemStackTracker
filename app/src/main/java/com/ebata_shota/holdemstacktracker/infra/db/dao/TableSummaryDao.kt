package com.ebata_shota.holdemstacktracker.infra.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ebata_shota.holdemstacktracker.infra.db.entity.TableSummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TableSummaryDao {
    @Query("SELECT * FROM table_summary_entities ORDER BY update_time DESC")
    fun getAllFlow(): Flow<List<TableSummaryEntity>>

    @Query("SELECT * FROM table_summary_entities WHERE table_id LIKE :tableIdString LIMIT 1")
    suspend fun findById(tableIdString: String): TableSummaryEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg entity: TableSummaryEntity)

    @Delete
    suspend fun delete(entity: TableSummaryEntity)
}