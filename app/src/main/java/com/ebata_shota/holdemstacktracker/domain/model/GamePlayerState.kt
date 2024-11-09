package com.ebata_shota.holdemstacktracker.domain.model

data class GamePlayerState(
    val id: PlayerId,
    val stack: Double,
    val isLeaved: Boolean
)