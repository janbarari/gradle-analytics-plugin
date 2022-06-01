package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.extension.isNotNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AnalyticsReportTest {

    @Test
    fun `check getters`() {
        val report = AnalyticsReport(
            "develop",
            "assembleDebug"
        )
        assertEquals("develop", report.branch)
        assertEquals("assembleDebug", report.requestedTasks)
        assertEquals(null, report.initializationReport)
        assertEquals(null, report.configurationReport)
    }

    @Test
    fun `check setters`() {
        val report = AnalyticsReport(
            "develop",
            "assembleDebug"
        )
        report.initializationReport = InitializationReport(listOf(), listOf(), 0)
        report.configurationReport = ConfigurationReport(listOf(), 0)

        assert(report.initializationReport.isNotNull())
        assert(report.configurationReport.isNotNull())
    }

    @Test
    fun `check toJson() returns json response`() {
        val report = AnalyticsReport(
            "develop",
            "assembleDebug"
        )
        assert(report.toJson().startsWith("{"))
        assert(report.toJson().endsWith("}"))
    }

}