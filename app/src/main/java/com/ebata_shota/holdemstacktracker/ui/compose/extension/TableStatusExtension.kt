package com.ebata_shota.holdemstacktracker.ui.compose.extension

import androidx.annotation.StringRes
import com.ebata_shota.holdemstacktracker.R
import com.ebata_shota.holdemstacktracker.domain.model.TableStatus

@StringRes
fun TableStatus.labelResId(): Int = when (this) {
    TableStatus.PREPARING -> R.string.table_status_preparing
    TableStatus.PAUSED -> R.string.table_status_paused
    TableStatus.PLAYING -> R.string.table_status_playing
}