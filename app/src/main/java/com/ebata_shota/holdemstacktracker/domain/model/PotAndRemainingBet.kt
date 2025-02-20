package com.ebata_shota.holdemstacktracker.domain.model

data class PotAndRemainingBet(
    val potList: List<Pot>,
    val pendingBetPerPlayerWithoutZero: Map<PlayerId, Int>,
)
