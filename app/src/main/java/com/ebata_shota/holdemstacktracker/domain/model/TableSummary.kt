package com.ebata_shota.holdemstacktracker.domain.model

import java.time.Instant

data class TableSummary(
    val tableId: TableId,
    val updateTime: Instant,
    val createTime: Instant
)