package com.ebata_shota.holdemstacktracker.domain.model

import java.time.Instant

data class TableSummary(
    val tableId: TableId,
    val blindText: String,
    val hostName: String,
    val updateTime: Instant,
    val createTime: Instant
)