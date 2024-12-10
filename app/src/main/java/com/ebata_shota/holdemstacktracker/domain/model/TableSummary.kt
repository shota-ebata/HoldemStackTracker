package com.ebata_shota.holdemstacktracker.domain.model

import androidx.annotation.StringRes
import java.time.Instant

data class TableSummary(
    val tableId: TableId,
    @StringRes
    val gameTypeTextResId: Int,
    val blindText: String,
    val hostName: String,
    val playerSize: String,
    val updateTime: Instant,
    val createTime: Instant
)