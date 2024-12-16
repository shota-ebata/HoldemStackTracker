package com.ebata_shota.holdemstacktracker.domain.model

data class GamePlayer(
    val id: PlayerId,
    val stack: Double,
    val isLeaved: Boolean
)