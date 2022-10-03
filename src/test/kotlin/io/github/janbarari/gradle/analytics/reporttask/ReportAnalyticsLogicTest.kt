package io.github.janbarari.gradle.analytics.reporttask

import io.github.janbarari.gradle.analytics.DatabaseConfig
import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import io.github.janbarari.gradle.analytics.database.SqliteDatabaseConnection
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.InitializationProcessMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesTimelineMetric
import io.github.janbarari.gradle.analytics.domain.usecase.GetMetricsUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.GetModulesTimelineUseCase
import io.github.janbarari.gradle.analytics.reporttask.exception.InvalidPropertyException
import io.github.janbarari.gradle.analytics.reporttask.exception.MissingPropertyException
import io.github.janbarari.gradle.utils.DateTimeUtils
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.runBlocking
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
            modules = emptyList()
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