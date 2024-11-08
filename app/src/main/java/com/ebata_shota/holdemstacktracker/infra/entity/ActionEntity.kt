package com.ebata_shota.holdemstacktracker.infra.entity

import androidx.annotation.Keep

@Keep
data class ActionState(
    val playerId: String,
    val type: String,
    val betSize: Double
)