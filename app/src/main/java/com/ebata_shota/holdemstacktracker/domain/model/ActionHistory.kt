package com.ebata_shota.holdemstacktracker.domain.model

import java.time.Instant

data class ActionHistory(
    val tableId: TableId,
    val actionId: ActionId,
    val hadSeen: Boolean,
    val timestamp: Instant,
)
