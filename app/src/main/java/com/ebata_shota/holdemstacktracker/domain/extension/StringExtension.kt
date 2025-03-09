package com.ebata_shota.holdemstacktracker.domain.extension

fun String.toIntOrZero(): Int {
    return try {
        this.toInt()
    } catch (e: IllegalArgumentException) {
        0
    }
}