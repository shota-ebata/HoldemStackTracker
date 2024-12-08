package com.ebata_shota.holdemstacktracker.infra.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "table_summary_entities")
data class TableSummaryEntity(
    @PrimaryKey
    @ColumnInfo("table_id")
    val tableId: String,
    @ColumnInfo("update_time")
    val updateTime: Instant,
    @ColumnInfo("create_time")
    val createTime: Instant
)
