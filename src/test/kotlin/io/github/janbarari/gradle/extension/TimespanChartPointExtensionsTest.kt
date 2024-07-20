/**
 * MIT License
 * Copyright (c) 2024 Mehdi Janbarari (@janbarari)
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.extension

import io.github.janbarari.gradle.analytics.domain.model.TimespanPoint
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimespanChartPointExtensionsTest {

    @Test
    fun `check mapToChartPoints mapper returns correct result`() {
        val timespanPoints = listOf(
            TimespanPoint(value = 1, from = 1640321493224),
            TimespanPoint(value = 2, from = 1650321493224),
            TimespanPoint(value = 3, from = 1660321493224, to = 1660421493224)
        )
        val result = timespanPoints.mapToChartPoints()

        assertEquals("24/12", result[0].description)
        assertEquals(1, result[0].value)
        assertEquals("18/04", result[1].description)
        assertEquals(2, result[1].value)
        assertEquals("12/08-13/08", result[2].description)
        assertEquals(3, result[2].value)
    }

    @Test
    fun `when minimize proceeds with data, expect the timespan inputs minimized`() {
        val timespanPoints = listOf(
            TimespanPoint(value = 1, from = 1640321493224),
            TimespanPoint(value = 2, from = 1650321493224),
            TimespanPoint(value = 3, from = 1660321493224),
            TimespanPoint(value = 1, from = 1640321493224),
            TimespanPoint(value = 2, from = 1650321493224),
            TimespanPoint(value = 3, from = 1660321493224),
            TimespanPoint(value = 1, from = 1640321493224),
            TimespanPoint(value = 2, from = 1650321493224),
            TimespanPoint(value = 3, from = 1660321493224),
            TimespanPoint(value = 1, from = 1640321493224),
            TimespanPoint(value = 2, from = 1650321493224),
            TimespanPoint(value = 3, from = 1660321493224),
        )
        val result = timespanPoints.minimize(5)
        assertTrue {
            result.size < 5
        }
    }

    @Test
    fun `when minimize proceeds with empty data, expect the original timespan data`() {
        val timespanPoints = emptyList<TimespanPoint>()
        val result = timespanPoints.minimize(5)
        assertTrue {
            result.isEmpty()
        }
    }

    @Test
    fun `when maxValue() invoked, expect the biggest timespan point`() {
        val samplePoints = listOf(
            TimespanPoint(1L, 0),
            TimespanPoint(100L, 0)
        )
        assertEquals(100L, samplePoints.maxValue())
    }

    @Test
    fun `when minValue() invoked, expect the biggest timespan point`() {
        val samplePoints = listOf(
            TimespanPoint(1L, 0),
            TimespanPoint(100L, 0)
        )
        assertEquals(1L, samplePoints.minValue())
    }

}
