package com.ebata_shota.holdemstacktracker.infra.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ebata_shota.holdemstacktracker.infra.db.dao.TableSummaryDao
import com.ebata_shota.holdemstacktracker.infra.db.entity.TableSummaryEntity

@Database(entities = [TableSummaryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tableDao(): TableSummaryDao
}