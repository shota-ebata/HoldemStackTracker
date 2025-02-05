package com.ebata_shota.holdemstacktracker.domain.model

data class PotSettlementInfo(
    val potId: PotId,
    val potSize: Int,
    val acquirerPlayerIds: List<PlayerId>,
)
