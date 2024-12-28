package com.ebata_shota.holdemstacktracker.domain.model

sealed interface Rule {
    val defaultStack: Int
    val minBetSize: Int

    data class RingGame(
        val sbSize: Int,
        val bbSize: Int,
        override val defaultStack: Int
    ) : Rule {
        override val minBetSize: Int = bbSize
    }
}
