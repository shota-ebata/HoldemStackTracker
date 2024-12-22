package com.ebata_shota.holdemstacktracker.domain.model

sealed interface Rule {
    val betViewMode: BetViewMode
    val defaultStack: Double
    val minBetSize: Double

    data class RingGame(
        val sbSize: Double,
        val bbSize: Double,
        override val betViewMode: BetViewMode,
        override val defaultStack: Double
    ) : Rule {
        override val minBetSize = bbSize
    }
}
