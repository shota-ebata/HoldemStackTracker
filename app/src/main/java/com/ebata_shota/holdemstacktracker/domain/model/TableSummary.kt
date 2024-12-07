package com.ebata_shota.holdemstacktracker.domain.model

data class TableSummary(
    val tableId: TableId,
    val updateTime: Long,
    val createTime: Long
)