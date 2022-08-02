package io.github.janbarari.gradle.analytics.reporttask

import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.InitializationProcessMetric
import io.github.janbarari.gradle.analytics.domain.usecase.GetMetricsUseCase
import io.github.janbarari.gradle.analytics.reporttask.exception.InvalidPropertyException
import io.github.janbarari.gradle.analytics.reporttask.exception.MissingPropertyException
import io.github.janbarari.gradle.extension.ensureNotNull
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReportAnalyticsLogicTest {

    private lateinit var injector: ReportAnalyticsInjector
    private lateinit var logic: ReportAnalyticsLogic

    @BeforeAll
    fun setup() {
        injector = ReportAnalyticsInjector(
            requestedTasks = "assembleDebug",
            isCI = false,
            databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
                local = sqlite {
                    path = "./build/"
                }
            },
            branch = "develop",
            outputPath = "./build/test/result/",
            projectName = "gradle-analytics-plugin"
        )
    }

    @Test
    fun `check ensureBranchArgumentValid() throws exception when branch is empty`() {
        logic = injector.provideReportAnalyticsLogic()
        assertThrows<MissingPropertyException> {
            logic.ensureBranchArgumentValid("")
        }
    }

    @Test
    fun `check ensureBranchArgumentValid() throws exception when branch is not valid`() {
        logic = injector.provideReportAnalyticsLogic()
        assertThrows<InvalidPropertyException> {
            logic.ensureBranchArgumentValid("mas ter")
        }
    }

    @Test
    fun `check ensureBranchArgumentValid() works fine`() {
        injector.provideReportAnalyticsLogic()
        assertDoesNotThrow {
            logic.ensureBranchArgumentValid("master")
        }
    }

    @Test
    fun `check ensurePeriodArgumentValid() throws exception when period is empty`() {
        injector.provideReportAnalyticsLogic()
        assertThrows<MissingPropertyException> {
            logic.ensurePeriodArgumentValid("")
        }
    }

    @Test
    fun `check ensurePeriodArgumentValid() throws exception when period is not valid`() {
        injector.provideReportAnalyticsLogic()
        assertThrows<InvalidPropertyException> {
            logic.ensurePeriodArgumentValid("ABC")
        }
    }

    @Test
    fun `check ensurePeriodArgumentValid() works fine`() {
        injector.provideReportAnalyticsLogic()
        assertDoesNotThrow {
            logic.ensurePeriodArgumentValid("3")
        }
    }

    @Test
    fun `check ensureTaskArgumentValid() throws exception when task is empty`() {
        injector.provideReportAnalyticsLogic()
        assertThrows<MissingPropertyException> {
            logic.ensureTaskArgumentValid("")
        }
    }

    @Test
    fun `check ensureTaskArgumentValid() works fine`() {
        injector.provideReportAnalyticsLogic()
        assertDoesNotThrow {
            logic.ensureTaskArgumentValid("assembleDebug")
        }
    }

    @Test
    fun `check generateReport() returns result when ran on Local and metrics are not empty`() = runBlocking {
        val mockedGetMetricsUseCase = mockk<GetMetricsUseCase>()
        coEvery { mockedGetMetricsUseCase.execute(3) } returns listOf(
            BuildMetric(
                "a",
                listOf("b"),
                100L,
                InitializationProcessMetric(1000L)
            )
        )

        logic = ReportAnalyticsLogicImp(
            mockedGetMetricsUseCase,
            ensureNotNull(injector.isCI),
            ensureNotNull(injector.outputPath),
            ensureNotNull(injector.projectName)
        )

        val result = logic.generateReport(
            "develop", "assembleDebug", 3
        )
        assert(result.contains("3 Months"))
        assert(result.contains("develop"))
        assert(result.contains("assembleDebug"))
    }

    @Test
    fun `check generateReport() returns result when ran on CI and metrics are not empty`() = runBlocking {
        injector.isCI = true

        val mockedGetMetricsUseCase = mockk<GetMetricsUseCase>()
        coEvery { mockedGetMetricsUseCase.execute(3) } returns listOf(
            BuildMetric(
                "a",
                listOf("b"),
                100L,
                InitializationProcessMetric(1000L)
            )
        )

        logic = ReportAnalyticsLogicImp(
            mockedGetMetricsUseCase,
            ensureNotNull(injector.isCI),
            ensureNotNull(injector.outputPath),
            ensureNotNull(injector.projectName)
        )

        val result = logic.generateReport(
            "develop", "assembleDebug", 3
        )
        assert(result.contains("3 Months"))
        assert(result.contains("develop"))
        assert(result.contains("assembleDebug"))
    }

    @Test
    fun `check saveReport() returns true`() = runBlocking {
        logic = injector.provideReportAnalyticsLogic()
        val result = logic.generateReport(
            "develop", "assembleDebug", 3
        )
        assert(logic.saveReport(result).isNotBlank())
    }

}