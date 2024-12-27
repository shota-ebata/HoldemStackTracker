package com.ebata_shota.holdemstacktracker.domain.extension

import com.ebata_shota.holdemstacktracker.domain.model.BetViewMode

fun Double.toHstString(betViewMode: BetViewMode): String {
    return when (betViewMode) {
        BetViewMode.Number -> "%,d".format(this.toInt())
        BetViewMode.BB -> this.toString()// TODO: 小数点第一位で丸めたい
    }
}