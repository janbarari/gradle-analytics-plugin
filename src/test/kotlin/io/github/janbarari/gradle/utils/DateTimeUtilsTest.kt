package io.github.janbarari.gradle.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DateTimeUtilsTest {

    @Test
    fun `check calculating the day in past 3 months`() {
        val currentDay = 1653696000000
        val result = DateTimeUtils.calculateDayInPastMonthsMs(currentDay, 3)
        assertEquals(1646006400000, result)
    }

}