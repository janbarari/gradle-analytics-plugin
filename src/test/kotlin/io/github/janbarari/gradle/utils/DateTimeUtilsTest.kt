package io.github.janbarari.gradle.utils

import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class DateTimeUtilsTest {

    @Test
    fun `check getDayStartMs() returns day starting time`() {
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
    fun `check getDayEndMs() returns day end time`() {
        val todayStartMs = DateTimeUtils.getDayStartMs()
        val todayEndMs = todayStartMs + DateTimeUtils.ONE_DAY_IN_MILLIS
        assertEquals(todayEndMs, DateTimeUtils.getDayEndMs())
    }

    @Test
    fun `check msToDateString() returns correct format`() {
        assertEquals("2022/06/01", DateTimeUtils.formatToDate(1654069596162))
    }

    @Test
    fun `check msToDateTimeString() returns correct format`() {
        assertEquals("2022/06/01 07:46 AM UTC", DateTimeUtils.formatToDateTime(1654069596162))
    }

}