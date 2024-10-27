package com.ebata_shota.holdemstacktracker.domain.model

sealed interface RuleState {
    data class LingGame(
        val sbSize: Float,
        val bbSize: Float
    ) : RuleState
}