package io.github.janbarari.gradle.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MathUtilsTest {

    @Test
    fun `check the longMean() with positive dataset`() {
        assertEquals(2L, MathUtils.longMean(1, 2, 3, 4))
    }

    @Test
    fun `check the longMean() with negative dataset`() {
        assertEquals(1L, MathUtils.longMean(-1, -2, 3, 4))
    }

    @Test
    fun `check the longMean() with empty dataset`() {
        assertEquals(0L, MathUtils.longMean())
    }

    @Test
    fun `check the longMedian() with empty dataset`() {
        assertEquals(0L, MathUtils.longMedian())
    }

    @Test
    fun `check the longMedian() with odd unsorted dataset`() {
        assertEquals(3L, MathUtils.longMedian(1000, 3, 1, 4, 2))
    }

    @Test
    fun `check the longMedian() with even unsorted dataset`() {
        assertEquals(3L, MathUtils.longMedian(1000, 3, 4, 2))
    }

}