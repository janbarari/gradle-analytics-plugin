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
package io.github.janbarari.gradle.analytics.reporttask

import io.github.janbarari.gradle.analytics.DatabaseConfig
import io.github.janbarari.gradle.analytics.database.SqliteDatabaseConnection
import io.github.janbarari.gradle.analytics.reporttask.exception.InvalidPropertyException
import io.github.janbarari.gradle.analytics.reporttask.exception.MissingPropertyException
import io.github.janbarari.gradle.utils.DateTimeUtils
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReportAnalyticsLogicTest {

    private lateinit var injector: ReportAnalyticsInjector

    @BeforeAll
    fun setup() {
        injector = ReportAnalyticsInjector(
            requestedTasks = "assembleDebug",
            isCI = false,
            databaseConfig = DatabaseConfig().apply {
                local = SqliteDatabaseConnection {
                    path = "./build"
                    name = "test"
                }
            },
            branch = "master",
            outputPath = "./build/test/result/",
            projectName = "gradle-analytics-plugin",
            modules = setOf(
                ":women",
                ":life",
                ":freedom"
            ),
            excludeModules = setOf()
        )
    }

    @Test
    fun `check ensureBranchArgumentValid() throws exception when branch is empty`() {
        val logic = injector.provideReportAnalyticsLogic()
        assertThrows<MissingPropertyException> {
            logic.ensureBranchArgumentValid("")
        }
    }

    @Test
    fun `check ensureBranchArgumentValid() throws exception when branch is not valid`() {
        val logic = injector.provideReportAnalyticsLogic()
        assertThrows<InvalidPropertyException> {
            logic.ensureBranchArgumentValid("mas ter")
        }
    }

    @Test
    fun `check ensureBranchArgumentValid() works fine`() {
        val logic = injector.provideReportAnalyticsLogic()
        assertDoesNotThrow {
            logic.ensureBranchArgumentValid("master")
        }
    }

    @Test
    fun `check ensurePeriodArgumentValid() throws exception when period is empty`() {
        val logic = injector.provideReportAnalyticsLogic()
        assertThrows<MissingPropertyException> {
            logic.ensurePeriodArgumentValid("")
        }
    }

    @Test
    fun `check ensurePeriodArgumentValid() throws exception when period is not valid`() {
        val logic = injector.provideReportAnalyticsLogic()
        assertThrows<InvalidPropertyException> {
            logic.ensurePeriodArgumentValid("ABC")
        }
    }

    @Test
    fun `check ensurePeriodArgumentValid() works fine`() {
        val logic = injector.provideReportAnalyticsLogic()
        assertDoesNotThrow {
            logic.ensurePeriodArgumentValid("3m")
        }
    }

    @Test
    fun `check ensureTaskArgumentValid() throws exception when task is empty`() {
        val logic = injector.provideReportAnalyticsLogic()
        assertThrows<MissingPropertyException> {
            logic.ensureTaskArgumentValid("")
        }
    }

    @Test
    fun `check ensureTaskArgumentValid() works fine`() {
        val logic = injector.provideReportAnalyticsLogic()
        assertDoesNotThrow {
            logic.ensureTaskArgumentValid("assembleDebug")
        }
    }

    @Test
    fun `check convertQueryToPeriod() throws exception when query is invalid`() {
        val logic = injector.provideReportAnalyticsLogic()
        assertThrows<InvalidPropertyException> {
            logic.convertQueryToPeriod("3dm")
        }
        assertThrows<InvalidPropertyException> {
            logic.convertQueryToPeriod("3d 1m")
        }
        assertThrows<InvalidPropertyException> {
            logic.convertQueryToPeriod("3y")
        }
        assertThrows<InvalidPropertyException> {
            logic.convertQueryToPeriod("1d 3d")
        }
        assertThrows<InvalidPropertyException> {
            logic.convertQueryToPeriod("1m 3m")
        }
        assertThrows<InvalidPropertyException> {
            logic.convertQueryToPeriod("1y 3y")
        }
    }

    @Test
    fun `check convertQueryToPeriod() return today period`() {
        mockkObject(DateTimeUtils)
        every { DateTimeUtils.getDayStartMs() } returns 0
        every { DateTimeUtils.getDayEndMs() } returns 24
        val logic = injector.provideReportAnalyticsLogic()
        assertEquals(0, logic.convertQueryToPeriod("today").first)
        assertEquals(24, logic.convertQueryToPeriod("today").second)
        unmockkObject(DateTimeUtils)
    }
}