package com.ebata_shota.holdemstacktracker.domain.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class IterableExtensionTest {

    @Test
    fun indexOfFirstOrNull_0() {
        val actual = listOf(1, 2, 3).indexOfFirstOrNull { it == 1 }
        val expected = 0
        assertEquals(expected, actual)
    }

    @Test
    fun indexOfFirstOrNull_2() {
        val actual = listOf(1, 2, 3).indexOfFirstOrNull { it == 3 }
        val expected = 2
        assertEquals(expected, actual)
    }

    @Test
    fun indexOfFirstOrNull_null() {
        val actual = listOf(1, 2, 3).indexOfFirstOrNull { it == 4 }
        val expected = null
        assertEquals(expected, actual)
    }

    @Test
    fun mapAtIndex_0() {
        val actual = listOf(1, 2, 3).mapAtIndex(index = 0) { it + 100 }
        val expected = listOf(101, 2, 3)
        assertEquals(expected, actual)
    }

    @Test
    fun mapAtIndex_2() {
        val actual = listOf(1, 2, 3).mapAtIndex(index = 2) { it + 100 }
        val expected = listOf(1, 2, 103)
        assertEquals(expected, actual)
    }

    @Test
    fun mapAtIndex_3() {
        val actual = listOf(1, 2, 3).mapAtIndex(index = 3) { it + 100 }
        val expected = listOf(1, 2, 3)
        assertEquals(expected, actual)
    }
}