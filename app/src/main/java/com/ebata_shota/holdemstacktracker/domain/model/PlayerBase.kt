package com.ebata_shota.holdemstacktracker.domain.model

data class PlayerBase(
    val id: PlayerId,
    val name: String,
    val stack: Int,
    val isLeaved: Boolean,
)