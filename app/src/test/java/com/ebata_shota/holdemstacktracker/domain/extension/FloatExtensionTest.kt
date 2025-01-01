package com.ebata_shota.holdemstacktracker.domain.extension

import junit.framework.Assert.assertEquals
import org.junit.Test

class FloatExtensionTest {
    @Test
    fun test_roundDigit() {
        val actual = 10.245f.roundDigit(1)
        val expected = 10.0f
        assertEquals(actual, expected)
    }

    @Test
    fun test_roundDigit_2() {
        val actual = 10.245f.roundDigit(2)
        val expected = 10.2f
        assertEquals(actual, expected)
    }

    @Test
    fun test_roundDigit_3() {
        val actual = 10.245f.roundDigit(3)
        val expected = 10.25f
        assertEquals(actual, expected)
    }
    @Test
    fun test_roundDigit_3_2() {
        val actual = 10.244f.roundDigit(3)
        val expected = 10.24f
        assertEquals(actual, expected)
    }
}