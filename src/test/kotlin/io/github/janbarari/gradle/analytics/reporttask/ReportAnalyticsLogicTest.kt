package io.github.janbarari.gradle.analytics.reporttask

import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import io.github.janbarari.gradle.analytics.reporttask.exception.InvalidPropertyException
import io.github.janbarari.gradle.analytics.reporttask.exception.MissingPropertyException
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReportAnalyticsLogicTest {

    private lateinit var injector: ReportAnalyticsInjector
    private lateinit var logic: ReportAnalyticsLogic

    @BeforeAll
    fun setup() {
        injector = ReportAnalyticsInjector(
            requestedTasks = "", isCI = false, databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
                local = sqlite {
                    path = "./"
                }
            }, branch = "", outputPath = "./test-result/", projectName = "gradle-analytics-plugin"
        )
        logic = injector.provideReportAnalyticsLogic()
    }

    @Test
    fun `check ensureBranchArgumentValid() throws exception when branch is empty`() {
        assertThrows<MissingPropertyException> {
            logic.ensureBranchArgumentValid("")
        }
    }

    @Test
    fun `check ensureBranchArgumentValid() throws exception when branch is not valid`() {
        assertThrows<InvalidPropertyException> {
            logic.ensureBranchArgumentValid("mas ter")
        }
    }

    @Test
    fun `check ensureBranchArgumentValid() works fine`() {
        assertDoesNotThrow {
            logic.ensureBranchArgumentValid("master")
        }
    }

    @Test
    fun `check ensurePeriodArgumentValid() throws exception when period is empty`() {
        assertThrows<MissingPropertyException> {
            logic.ensurePeriodArgumentValid("")
        }
    }

    @Test
    fun `check ensurePeriodArgumentValid() throws exception when period is not valid`() {
        assertThrows<InvalidPropertyException> {
            logic.ensurePeriodArgumentValid("ABC")
        }
    }

    @Test
    fun `check ensurePeriodArgumentValid() works fine`() {
        assertDoesNotThrow {
            logic.ensurePeriodArgumentValid("3")
        }
    }

    @Test
    fun `check ensureTaskArgumentValid() throws exception when task is empty`() {
        assertThrows<MissingPropertyException> {
            logic.ensureTaskArgumentValid("")
        }
    }

    @Test
    fun `check ensureTaskArgumentValid() works fine`() {
        assertDoesNotThrow {
            logic.ensureTaskArgumentValid("assembleDebug")
        }
    }

    @Test
    fun `check generateReport() returns rendered HTML`() {
        val result = logic.generateReport(
            "develop", "assembleDebug", 3
        )
        assert(result.contains("3 Months"))
        assert(result.contains("develop"))
        assert(result.contains("assembleDebug"))
    }

    @Test
    fun `check saveReport() returns true`() {
        val result = logic.generateReport(
            "develop", "assembleDebug", 3
        )
        assert(logic.saveReport(result))
    }

}