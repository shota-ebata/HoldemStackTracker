package com.ebata_shota.holdemstacktracker.infra.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ebata_shota.holdemstacktracker.infra.db.entity.PhaseHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhaseHistoryDao {
    @Query("SELECT * FROM phase_history_entities ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<PhaseHistoryEntity>>

    @Query("SELECT * FROM phase_history_entities WHERE table_id LIKE :tableIdString AND phase_id LIKE :phaseIdString LIMIT 1")
    suspend fun findById(
        tableIdString: String,
        phaseIdString: String,
    ): PhaseHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg entity: PhaseHistoryEntity)

    @Delete
    suspend fun delete(entity: PhaseHistoryEntity)
}
