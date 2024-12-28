package com.ebata_shota.holdemstacktracker.domain.model

data class Pot(
    val id: Long,
    val potNumber: Long,
    val potSize: Int,
    val involvedPlayerIds: List<PlayerId>,
    val isClosed: Boolean
)