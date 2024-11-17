package com.ebata_shota.holdemstacktracker.ui.extension

import androidx.annotation.StringRes
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode

@StringRes
fun BetViewMode.labelResId(): Int {
    return when(this) {
        BetViewMode.Number -> R.string.bet_view_type_chip_number
        BetViewMode.BB -> R.string.bet_view_type_chip_bb
    }
}