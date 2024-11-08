package com.ebata_shota.holdemstacktracker.infra.entity

import androidx.annotation.Keep

@Keep
data class GamePlayerEntity(
    val stack: Double,
    val isLeaved: Boolean
)