package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.extension.isNotNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ReportTest {

    @Test
    fun `check getters`() {
        val report = Report(
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
        val report = Report(
            "develop",
            "assembleDebug"
        )
        report.initializationReport = InitializationReport(listOf(), 0, 0)
        report.configurationReport = ConfigurationReport(listOf(), 0, 0)

        assert(report.initializationReport.isNotNull())
        assert(report.configurationReport.isNotNull())
    }

    @Test
    fun `check toJson() returns json response`() {
        val report = Report(
            "develop",
            "assembleDebug"
        )
        assert(report.toJson().startsWith("{"))
        assert(report.toJson().endsWith("}"))
    }

}