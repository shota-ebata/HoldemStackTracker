package com.ebata_shota.holdemstacktracker.infra.extension

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode
import com.ebata_shota.holdemstacktracker.domain.model.Rule

fun Rule.blindText(): String {
    return when (this) {
        is Rule.RingGame -> {
            when (betViewMode) {
                BetViewMode.Number -> "${sbSize.toInt()}/${bbSize.toInt()}"
                BetViewMode.BB -> "$sbSize/$bbSize"
            }
        }
    }
}