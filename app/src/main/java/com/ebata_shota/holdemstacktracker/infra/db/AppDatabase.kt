package com.ebata_shota.holdemstacktracker.infra.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ebata_shota.holdemstacktracker.infra.db.convert.GameTypeConverter
import com.ebata_shota.holdemstacktracker.infra.db.convert.InstantConverter
import com.ebata_shota.holdemstacktracker.infra.db.dao.ActionHistoryDao
import com.ebata_shota.holdemstacktracker.infra.db.dao.PhaseHistoryDao
import com.ebata_shota.holdemstacktracker.infra.db.dao.TableSummaryDao
import com.ebata_shota.holdemstacktracker.infra.db.entity.ActionHistoryEntity
import com.ebata_shota.holdemstacktracker.infra.db.entity.PhaseHistoryEntity
import com.ebata_shota.holdemstacktracker.infra.db.entity.TableSummaryEntity

@Database(
    entities = [
        TableSummaryEntity::class,
        ActionHistoryEntity::class,
        PhaseHistoryEntity::class,
    ],
    version = 1
)
@TypeConverters(
    InstantConverter::class,
    GameTypeConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tableSummaryDao(): TableSummaryDao
    abstract fun phaseHistoryDao(): PhaseHistoryDao
    abstract fun actionHistoryDao(): ActionHistoryDao
}
