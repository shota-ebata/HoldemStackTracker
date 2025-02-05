package com.ebata_shota.holdemstacktracker.domain.model

data class Pot(
    val id: PotId,
    val potNumber: Int,
    val potSize: Int,
    val involvedPlayerIds: List<PlayerId>,
    val isClosed: Boolean
)