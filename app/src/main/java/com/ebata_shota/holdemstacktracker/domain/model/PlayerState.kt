package com.ebata_shota.holdemstacktracker.domain.model

data class PlayerState(
    val id: PlayerId,
    val name: String,
    val stack: Float,
    val isLeaved: Boolean
)