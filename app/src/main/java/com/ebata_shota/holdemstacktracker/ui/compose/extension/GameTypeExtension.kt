package com.ebata_shota.holdemstacktracker.ui.compose.extension

import androidx.annotation.StringRes
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.GameType

@StringRes
fun GameType.labelResId(): Int {
    return when(this) {
        GameType.RingGame -> R.string.game_type_ring
    }
}