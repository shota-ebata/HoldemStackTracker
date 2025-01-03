package com.ebata_shota.holdemstacktracker.infra.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.Instant

@Entity(
    tableName = "phase_history_entities",
    primaryKeys = ["table_id", "phase_id"]
)
data class PhaseHistoryEntity(
    @ColumnInfo("table_id")
    val tableId: String,
    @ColumnInfo("phase_id")
    val phaseId: String,
    @ColumnInfo("is_finished")
    val isFinished: Boolean,
    @ColumnInfo("timestamp")
    val timestamp: Instant,
)
