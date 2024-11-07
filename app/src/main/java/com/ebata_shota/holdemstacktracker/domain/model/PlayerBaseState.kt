package com.ebata_shota.holdemstacktracker.domain.model

data class PlayerBaseState(
    val id: PlayerId,
    val name: String,
    val stack: Double
)