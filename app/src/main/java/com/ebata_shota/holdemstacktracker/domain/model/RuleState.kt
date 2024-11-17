package com.ebata_shota.holdemstacktracker.domain.model

sealed interface RuleState {
    val betViewMode: BetViewMode
    val defaultStack: Double

    data class RingGame(
        val sbSize: Double,
        val bbSize: Double,
        override val betViewMode: BetViewMode,
        override val defaultStack: Double
    ) : RuleState
}
