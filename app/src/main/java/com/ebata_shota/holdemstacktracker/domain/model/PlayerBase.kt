package com.ebata_shota.holdemstacktracker.domain.model

import java.time.Instant

data class PlayerBase(
    val id: PlayerId,
    val name: String,
    val stack: Int,
    val isSeated: Boolean,
    val isConnected: Boolean,
    val lostConnectTimestamp: Instant?,
)