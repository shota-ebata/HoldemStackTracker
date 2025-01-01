package com.ebata_shota.holdemstacktracker.domain.extension

import junit.framework.Assert.assertEquals
import org.junit.Test

class DoubleExtensionTest {

    @Test
    fun test_roundDigit() {
        val actual = 10.245.roundDigit(1)
        val expected = 10.0
        assertEquals(actual, expected, 0.0)
    }

    @Test
    fun test_roundDigit_2() {
        val actual = 10.245.roundDigit(2)
        val expected = 10.2
        assertEquals(actual, expected, 0.0)
    }

    @Test
    fun test_roundDigit_3() {
        val actual = 10.245.roundDigit(3)
        val expected = 10.25
        assertEquals(actual, expected, 0.0)
    }
    @Test
    fun test_roundDigit_3_2() {
        val actual = 10.244.roundDigit(3)
        val expected = 10.24
        assertEquals(actual, expected, 0.0)
    }
}