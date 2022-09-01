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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor

/**
 * A collection of datetime functions.
 */
object DateTimeUtils {

    const val ONE_DAY_IN_MILLIS = 86_400_000
    val DEFAULT_ZONE: ZoneId = ZoneId.of("UTC")

    /**
     * Calculates the current day start time in milliseconds.
     *
     * Note: Timezone is UTC
     */
    fun getDayStartMs(): Long {
        return LocalDate.now().atStartOfDay(DEFAULT_ZONE).toEpochSecond() * 1000
    }

    /**
     * Calculates the current day end time in milliseconds.
     *
     * Note: Timezone is UTC
     */
    fun getDayEndMs(): Long {
        return getDayStartMs() + ONE_DAY_IN_MILLIS
    }

    /**
     * Calculates the before month(s) time in milliseconds.
     *
     * Note: Timezone is UTC
     */
    fun calculateDayInPastMonthsMs(from: Long, months: Long): Long {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(from), DEFAULT_ZONE)
            .minusMonths(months)
            .atZone(DEFAULT_ZONE)
            .toEpochSecond() * 1000
    }

    /**
     * Converts time in milliseconds to dedicated datetime pattern.
     */
    fun format(timeInMs: Long, pattern: String): String {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timeInMs), DEFAULT_ZONE)
            .format(DateTimeFormatter.ofPattern(pattern))
    }

    /**
     * Converts time in milliseconds to a formatted date string.
     */
    fun formatToDate(timeInMs: Long): String {
        return format(timeInMs ,"yyyy/MM/dd")
    }

    /**
     * Converts time in milliseconds to a formatted date & time string.
     */
    fun formatToDateTime(timeInMs: Long): String {
        return format(timeInMs, "yyyy/MM/dd HH:mm a 'UTC'")
    }

    fun convertSecondsToHumanReadableTime(seconds: Long): String {
        val numYears = floor(seconds / 31536000F)
        val numDays = floor((seconds % 31536000) / 86400F)
        val numHours = floor(((seconds % 31536000F) % 86400F) / 3600F)
        val numMinutes = floor((((seconds % 31536000F) % 86400F) % 3600F) / 60F)
        if (numYears > 0) {
            return "${numYears}y ${numDays}d"
        }
        if (numDays > 0) {
            return "${numDays}d ${numHours}h"
        }
        if (numHours > 0) {
            return "${numHours}h ${numMinutes}m"
        }
        return "${numMinutes}m"
    }

}
