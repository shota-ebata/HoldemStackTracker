package com.ebata_shota.holdemstacktracker.domain.extension

fun <T> List<T>.rearrangeListFromIndex(startIndex: Int): List<T> {
    if (this.isEmpty() || startIndex !in this.indices) return this
    // startIndex から最後までの部分と、先頭から startIndex までの部分を結合
    return this.subList(startIndex, this.size) + this.subList(0, startIndex)
}