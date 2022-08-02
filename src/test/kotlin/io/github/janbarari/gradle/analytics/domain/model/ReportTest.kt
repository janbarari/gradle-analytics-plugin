package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.analytics.domain.model.report.ConfigurationProcessReport
import io.github.janbarari.gradle.analytics.domain.model.report.InitializationProcessReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
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
        assertEquals(null, report.initializationProcessReport)
        assertEquals(null, report.configurationProcessReport)
    }

    @Test
    fun `check setters`() {
        val report = Report(
            "develop",
            "assembleDebug"
        )
        report.initializationProcessReport = InitializationProcessReport(listOf(), listOf(),0, 0)
        report.configurationProcessReport = ConfigurationProcessReport(listOf(), listOf(), 0, 0)

        assert(report.initializationProcessReport.isNotNull())
        assert(report.configurationProcessReport.isNotNull())
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