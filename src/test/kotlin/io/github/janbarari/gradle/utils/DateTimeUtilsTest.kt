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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.utils

import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class DateTimeUtilsTest {

    @Test
    fun `when getDayStartMs() invoked, expect day start time`() {
        val todayStartMs = DateTimeUtils.getDayStartMs()
        println(todayStartMs)
        val date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(todayStartMs), DateTimeUtils.DEFAULT_ZONE)
        val hour = date.hour
        val min = date.minute
        val sec = date.second
        assertEquals(0, hour)
        assertEquals(0, min)
        assertEquals(0, sec)
    }

    @Test
    fun `when getDayEndMs() invoked, expect day end time`() {
        val todayStartMs = DateTimeUtils.getDayStartMs()
        val todayEndMs = todayStartMs + DateTimeUtils.ONE_DAY_IN_MILLIS
        assertEquals(todayEndMs, DateTimeUtils.getDayEndMs())
    }

    @Test
    fun `when msToDateString() invoked, expect result in correct format`() {
        assertEquals("2022/06/01", DateTimeUtils.formatToDate(1654069596162))
    }

    @Test
    fun `when msToDateTimeString() invoked, expect result in correct format`() {
        assertEquals("2022/06/01 07:46 AM UTC", DateTimeUtils.formatToDateTime(1654069596162))
    }

    @Test
    fun `when convertDateToEpochMilli() invoked, expect get date in UTC milliseconds`() {
        val dateInMillis = DateTimeUtils.convertDateToEpochMilli("2022/11/18")
        assertEquals(1668729600000L, dateInMillis)
    }

    @Test
    fun `when convertSecondsToHumanReadableTime() invoked, validate the result`() {
        DateTimeUtils.convertSecondsToHumanReadableTime(20).also {
            assertEquals("20s", it)
        }
        DateTimeUtils.convertSecondsToHumanReadableTime(100).also {
            assertEquals("1m 40s", it)
        }
        DateTimeUtils.convertSecondsToHumanReadableTime(1000).also {
            assertEquals("16m 40s", it)
        }
        DateTimeUtils.convertSecondsToHumanReadableTime(10_000).also {
            assertEquals("2h 46m", it)
        }
        DateTimeUtils.convertSecondsToHumanReadableTime(100_000).also {
            assertEquals("1d 3h", it)
        }
        DateTimeUtils.convertSecondsToHumanReadableTime(1_000_000).also {
            assertEquals("11d 13h", it)
        }
        DateTimeUtils.convertSecondsToHumanReadableTime(900_000_000).also {
            assertEquals("28y 196d", it)
        }
    }

}