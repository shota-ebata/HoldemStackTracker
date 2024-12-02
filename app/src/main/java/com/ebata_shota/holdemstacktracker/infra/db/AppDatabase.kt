package com.ebata_shota.holdemstacktracker.infra.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ebata_shota.holdemstacktracker.infra.db.dao.TableDao
import com.ebata_shota.holdemstacktracker.infra.db.entity.TableEntity

@Database(entities = [TableEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tableDao(): TableDao
}