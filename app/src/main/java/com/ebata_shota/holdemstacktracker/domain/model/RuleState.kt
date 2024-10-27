package com.ebata_shota.holdemstacktracker.domain.model

sealed interface RuleState {
    val betViewMode: BetViewMode

    data class LingGame(
        val sbSize: Float,
        val bbSize: Float,
        override val betViewMode: BetViewMode
    ) : RuleState
}
