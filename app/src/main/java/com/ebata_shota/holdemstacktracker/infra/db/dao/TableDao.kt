package com.ebata_shota.holdemstacktracker.infra.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ebata_shota.holdemstacktracker.infra.db.entity.TableEntity

@Dao
interface TableDao {
    @Query("SELECT * FROM table_entities")
    suspend fun getAll(): List<TableEntity>

    @Query("SELECT * FROM table_entities WHERE table_id LIKE :tableIdString LIMIT 1")
    suspend fun findById(tableIdString: String): TableEntity

    @Insert
    suspend fun insert(vararg entity: TableEntity)

    @Delete
    suspend fun delete(entity: TableEntity)
}