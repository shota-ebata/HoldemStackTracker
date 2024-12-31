package com.ebata_shota.holdemstacktracker.infra.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ebata_shota.holdemstacktracker.infra.db.convert.InstantConverter
import com.ebata_shota.holdemstacktracker.infra.db.dao.ActionHistoryDao
import com.ebata_shota.holdemstacktracker.infra.db.dao.TableSummaryDao
import com.ebata_shota.holdemstacktracker.infra.db.entity.ActionHistoryEntity
import com.ebata_shota.holdemstacktracker.infra.db.entity.TableSummaryEntity

@Database(
    entities = [
        TableSummaryEntity::class,
        ActionHistoryEntity::class
    ],
    version = 1
)
@TypeConverters(
    InstantConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tableSummaryDao(): TableSummaryDao
    abstract fun actionDao(): ActionHistoryDao
}