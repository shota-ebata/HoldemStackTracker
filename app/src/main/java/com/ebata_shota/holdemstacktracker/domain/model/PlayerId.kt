package com.ebata_shota.holdemstacktracker.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PlayerId(
    val value: String
) : Parcelable
