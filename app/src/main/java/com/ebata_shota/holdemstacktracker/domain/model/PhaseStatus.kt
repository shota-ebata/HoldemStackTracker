package com.ebata_shota.holdemstacktracker.domain.model

import androidx.annotation.Keep

@Keep
enum class PhaseStatus {
    Active,
    Close,
    AllInClose;

    companion object {
        fun of(label: String): PhaseStatus {
            return entries.find { it.name == label }
                ?: throw IllegalArgumentException("Unsupported label= $label")
        }
    }
}