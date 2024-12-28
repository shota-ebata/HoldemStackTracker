package com.ebata_shota.holdemstacktracker.infra.extension

import androidx.annotation.StringRes
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.Rule

fun Rule.blindText(): String {
    return when (this) {
        is Rule.RingGame -> {
            "${"%,d".format(sbSize)}/${"%,d".format(bbSize)}"
        }
    }
}

@StringRes
fun Rule.gameTextResId(): Int {
    return when (this) {
        is Rule.RingGame -> R.string.game_type_ring
    }
}