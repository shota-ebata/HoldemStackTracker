package com.ebata_shota.holdemstacktracker.domain.model

data class PodState(
    val id: Long,
    val podNumber: Int,
    val podSize: Float,
    val involvedPlayerIds: List<PlayerId>,
    val isClosed: Boolean
)