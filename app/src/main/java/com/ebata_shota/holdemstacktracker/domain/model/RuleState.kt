package com.ebata_shota.holdemstacktracker.domain.model

sealed interface RuleState {
    val betViewMode: BetViewMode

    data class LingGame(
        val sbSize: Double,
        val bbSize: Double,
        override val betViewMode: BetViewMode
    ) : RuleState
}
