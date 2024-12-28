package com.ebata_shota.holdemstacktracker.infra.entity

import androidx.annotation.Keep

@Keep
data class GamePlayerEntity(
    val stack: Int,
    val isLeaved: Boolean
)