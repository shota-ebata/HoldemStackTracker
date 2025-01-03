package com.ebata_shota.holdemstacktracker.infra.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ebata_shota.holdemstacktracker.infra.db.entity.ActionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionHistoryDao {

    @Query("SELECT * FROM action_history_entities ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<ActionHistoryEntity>>

    @Query("SELECT * FROM action_history_entities WHERE table_id LIKE :tableIdString AND action_id LIKE :actionIdString LIMIT 1")
    suspend fun findById(
        tableIdString: String,
        actionIdString: String,
    ): ActionHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg entity: ActionHistoryEntity)

    @Delete
    suspend fun delete(entity: ActionHistoryEntity)
}