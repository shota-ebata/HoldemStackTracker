package com.ebata_shota.holdemstacktracker.domain.model

import java.time.Instant

data class PhaseHistory(
    val tableId: TableId,
    val phaseId: PhaseId,
    val isFinished: Boolean,
    val timestamp: Instant,
)
