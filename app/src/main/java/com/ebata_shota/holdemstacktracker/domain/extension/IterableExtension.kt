package com.ebata_shota.holdemstacktracker.domain.extension

inline fun <T> Iterable<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    return this.indexOfFirst(predicate).takeIf { it != -1 }
}

inline fun <T> Iterable<T>.mapAtIndex(index: Int, transform: (T) -> T): List<T> {
    return this.mapIndexed { i, t ->
        if (i == index) {
            transform(t)
        } else {
            t
        }
    }
}