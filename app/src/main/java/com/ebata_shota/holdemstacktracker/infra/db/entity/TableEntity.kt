package com.ebata_shota.holdemstacktracker.infra.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_entities")
data class TableEntity(
    @PrimaryKey
    @ColumnInfo("table_id")
    val tableId: String,
    @ColumnInfo("update_time")
    val updateTime: Long
)
