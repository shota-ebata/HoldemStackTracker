package com.ebata_shota.holdemstacktracker.infra.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.Instant

@Entity(
    tableName = "action_history_entities",
    primaryKeys = ["table_id", "action_id"]
)
data class ActionHistoryEntity(
    @ColumnInfo("table_id")
    val tableId: String,
    @ColumnInfo("action_id")
    val actionId: String,
    @ColumnInfo("had_seen")
    val hadSeen: Boolean,
    @ColumnInfo("timestamp")
    val timestamp: Instant,
)