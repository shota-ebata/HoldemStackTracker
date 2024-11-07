package com.ebata_shota.holdemstacktracker.domain.model

data class GamePlayerState(
    val id: PlayerId,
    val name: String,
    val stack: Double,
    val isLeaved: Boolean
)