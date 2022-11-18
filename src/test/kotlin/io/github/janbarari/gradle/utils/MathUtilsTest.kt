/**
 * MIT License
 * Copyright (c) 2022 Mehdi Janbarari (@janbarari)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.utils

import io.github.janbarari.gradle.analytics.domain.model.TimeSlot
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MathUtilsTest {

    @Test
    fun `when longMean() invoked with a positive dataset, validate the result`() {
        assertEquals(2L, MathUtils.longMean(1, 2, 3, 4))
    }

    @Test
    fun `when longMean() invoked with a negative dataset, validate the result`() {
        assertEquals(1L, MathUtils.longMean(-1, -2, 3, 4))
    }

    @Test
    fun `when longMean() invoked with empty dataset, validate the result`() {
        assertEquals(0L, MathUtils.longMean())
    }


    @Test
    fun `when longMedian() invoked with empty dataset, validate the result`() {
        assertEquals(0L, MathUtils.longMedian())
    }

    @Test
    fun `when longMedian() invoked with odd unsorted dataset, validate the result`() {
        assertEquals(3L, MathUtils.longMedian(1000, 3, 1, 4, 2))
    }

    @Test
    fun `when longMedian() invoked with even unsorted dataset, validate the result`() {
        assertEquals(3L, MathUtils.longMedian(1000, 3, 4, 2))
    }


    @Test
    fun `when floatMedian() invoked with empty dataset, validate the result`() {
        assertEquals(0F, MathUtils.floatMedian())
    }

    @Test
    fun `when floatMedian() invoked with odd unsorted dataset, validate the result`() {
        assertEquals(3F, MathUtils.floatMedian(1000F, 3F, 1F, 4F, 2F))
    }

    @Test
    fun `when floatMedian() invoked with even unsorted dataset, validate the result`() {
        assertEquals(3.5F, MathUtils.floatMedian(1000F, 3F, 4F, 2F))
    }


    @Test
    fun `when sumWithPercentage() invoked with positive percentage, validate the result`() {
        val defaultValue = 100L
        val newValue = MathUtils.sumWithPercentage(defaultValue, 25)
        assertEquals(125L, newValue)
    }

    @Test
    fun `when sumWithPercentage() invoked with zero percentage, validate the result`() {
        val defaultValue = 100L
        val newValue = MathUtils.sumWithPercentage(defaultValue, 0)
        assertEquals(100L, newValue)
    }

    @Test
    fun `when sumWithPercentage() invoked with negative percentage, validate the result`() {
        val defaultValue = 100L
        val newValue = MathUtils.sumWithPercentage(defaultValue, -25)
        assertEquals(75L, newValue)
    }


    @Test
    fun `when deductWithPercentage() invoked with positive percentage, validate the result`() {
        val defaultValue = 100L
        val newValue = MathUtils.deductWithPercentage(defaultValue, 25)
        assertEquals(75L, newValue)
    }

    @Test
    fun `when deductWithPercentage() invoked with zero percentage, validate the result`() {
        val defaultValue = 100L
        val newValue = MathUtils.deductWithPercentage(defaultValue, 0)
        assertEquals(100L, newValue)
    }

    @Test
    fun `when deductWithPercentage() invoked with negative percentage, validate the result`() {
        val defaultValue = 100L
        val newValue = MathUtils.deductWithPercentage(defaultValue, -25)
        assertEquals(125L, newValue)
    }

    @Test
    fun `when calculateTimeSlotNonParallelDurationInMillis() invoked, validate the result`() {
        val sampleTimeSlots = arrayListOf<TimeSlot>(
            TimeSlot.create(10, 100),
            TimeSlot.create(25, 80),
            TimeSlot.create(90, 110),
            TimeSlot.create(120, 130),
            TimeSlot.create(150, 250),
            TimeSlot.create(0, 25),
            TimeSlot.create(0, 9),
        )
        assertEquals(220L, MathUtils.calculateTimeSlotNonParallelDurationInMillis(sampleTimeSlots))
    }

}