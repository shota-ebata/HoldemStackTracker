package com.ebata_shota.holdemstacktracker.domain.model

import androidx.annotation.Keep

@Keep
enum class TableStatus {
    PREPARING,
    PAUSED,
    PLAYING;

    companion object  {
        fun of(label: String): TableStatus {
            return TableStatus.entries.find { it.name == label }
                ?: throw IllegalArgumentException("Unsupported label= $label")
        }
    }
}