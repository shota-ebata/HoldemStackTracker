package com.ebata_shota.holdemstacktracker.domain.model

import androidx.annotation.Keep

@Keep
enum class BetViewMode {
    Number,
    BB;

    companion object {
        fun of(label: String): BetViewMode {
            return entries.find { it.name == label }
                ?: throw IllegalArgumentException("Unsupported label= $label")
        }
    }
}