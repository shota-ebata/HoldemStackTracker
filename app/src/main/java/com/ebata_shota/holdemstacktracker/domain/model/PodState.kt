package com.ebata_shota.holdemstacktracker.domain.model

data class PodState(
    val id: Long,
    val podNumber: Long,
    val podSize: Double,
    val involvedPlayerIds: List<PlayerId>,
    val isClosed: Boolean
)