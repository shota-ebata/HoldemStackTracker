package com.ebata_shota.holdemstacktracker.domain.extension

inline fun <T> Iterable<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    return this.indexOfFirst(predicate).takeIf { it != -1 }
}

inline fun <T> Iterable<T>.mapAtIndex(index: Int, transform: (T) -> T): List<T> {
    val updatedMutableList = toMutableList()
    updatedMutableList[index] = transform(updatedMutableList[index])
    return updatedMutableList
}

inline fun <T> Iterable<T>.mapAtFind(predicate: (T) -> Boolean, transform: (T) -> T): List<T> {
    val updatedMutableList = toMutableList()
    val index = indexOfFirst(predicate)
    updatedMutableList[index] = transform(updatedMutableList[index])
    return updatedMutableList
}