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
package io.github.janbarari.gradle.analytics.domain.model.report

import io.github.janbarari.gradle.extension.isNotNull
import io.mockk.mockk
import org.gradle.internal.impldep.com.google.gson.JsonParser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
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
        assertEquals(null, report.executionProcessReport)
        assertEquals(null, report.overallBuildProcessReport)
        assertEquals(null, report.modulesSourceCountReport)
        assertEquals(null, report.modulesMethodCountReport)
        assertEquals(null, report.cacheHitReport)
        assertEquals(null, report.successBuildRateReport)
        assertEquals(null, report.dependencyResolveProcessReport)
        assertEquals(null, report.parallelExecutionRateReport)
    }

    @Test
    fun `check setters`() {
        val report = Report(
            "develop",
            "assembleDebug"
        ).apply {
            initializationProcessReport = mockk()
            configurationProcessReport = mockk()
            executionProcessReport = mockk()
            overallBuildProcessReport = mockk()
            modulesSourceCountReport = mockk()
            modulesMethodCountReport = mockk()
            cacheHitReport = mockk()
            successBuildRateReport = mockk()
            dependencyResolveProcessReport = mockk()
            parallelExecutionRateReport = mockk()
        }

        assert(report.initializationProcessReport.isNotNull())
        assert(report.configurationProcessReport.isNotNull())
        assert(report.executionProcessReport.isNotNull())
        assert(report.overallBuildProcessReport.isNotNull())
        assert(report.modulesSourceCountReport.isNotNull())
        assert(report.modulesMethodCountReport.isNotNull())
        assert(report.cacheHitReport.isNotNull())
        assert(report.successBuildRateReport.isNotNull())
        assert(report.dependencyResolveProcessReport.isNotNull())
        assert(report.parallelExecutionRateReport.isNotNull())
    }

    @Test
    fun `check toJson() returns json response`() {
        val report = Report(
            "develop",
            "assembleDebug"
        )
        assertDoesNotThrow {
            JsonParser.parseString(report.toJson())
        }
    }

}