/**
 * MIT License
 * Copyright (c) 2022 Mehdi Janbarari (@janbarari)
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.metric.modulescrashcount.report

import io.github.janbarari.gradle.analytics.domain.model.metric.ModulesCrashCountMetric
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesCrashCountReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RenderModulesCrashCountReportStageTest {

    @Test
    fun `check render when report is not available`() = runBlocking {
        val report = Report("main", "assemble")
        val stage = RenderModulesCrashCountReportStage(report)

        val renderTemplate = "%modules-crash-count-metric%"
        val result = stage.process(renderTemplate)

        val expectedAnswer = "<p>Modules Crash Count is not available!</p><div class=\"space\"></div>"
        assertEquals(expectedAnswer, result)
    }

    @Test
    fun `check render when report is available`() = runBlocking {
        val report = Report("main", "assemble")
        report.modulesCrashCountReport = ModulesCrashCountReport(
            listOf(
                ModulesCrashCountMetric.ModuleCrash(":woman", 10),
                ModulesCrashCountMetric.ModuleCrash(":life", 4),
                ModulesCrashCountMetric.ModuleCrash(":freedom", 22)
            )
        )

        val stage = RenderModulesCrashCountReportStage(report)

        val renderTemplate = "%modules-crash-count-metric%"
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("labels: [\":freedom\",\":woman\",\":life\"]")
        }

        assertTrue {
            result.contains("data: [22, 10, 4]")
        }
    }

}
