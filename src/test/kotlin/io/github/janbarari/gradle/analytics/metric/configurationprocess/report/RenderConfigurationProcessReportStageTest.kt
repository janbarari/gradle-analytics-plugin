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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.metric.configurationprocess.report

import io.github.janbarari.gradle.TowerMockImpl
import io.github.janbarari.gradle.analytics.domain.model.TimespanPoint
import io.github.janbarari.gradle.analytics.domain.model.report.ConfigurationProcessReport
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RenderConfigurationProcessReportStageTest {

    @Test
    fun `check render when report is null`() = runBlocking {
        val report = Report("main", "assemble")

        val renderTemplate = "%configuration-process-metric%"
        val stage = RenderConfigurationProcessReportStage(TowerMockImpl(), report)
        val result = stage.process(renderTemplate)

        val expectedAnswer = "<p>Configuration Process is not available!</p><div class=\"space\"></div>"
        assertEquals(expectedAnswer, result)
    }

    @Test
    fun `check render when report is available`() = runBlocking {
        val report = Report("main", "assemble")
        report.configurationProcessReport = ConfigurationProcessReport(
            medianValues = listOf(
                TimespanPoint(
                    value = 1000L,
                    from = 1668836798265,
                    to = 1668936974389
                )
            ),
            meanValues = listOf(
                TimespanPoint(
                    value = 1000L,
                    from = 1668836798265,
                    to = 1668936974389
                )
            )
        )

        val renderTemplate = "%configuration-process-metric%"
        val stage = RenderConfigurationProcessReportStage(TowerMockImpl(), report)
        val result = stage.process(renderTemplate)

        assertTrue {
            result.contains("labels: [\"19/11-20/11\"]")
        }

        assertTrue {
            result.contains("datasets: [\n" +
                    "                  {\n" +
                    "                    label: \"Median\",\n" +
                    "                    fill: false,\n" +
                    "                    backgroundColor: shadeColor(getColor(), 25),\n" +
                    "                    borderColor: getColor(),\n" +
                    "                    data: [1000],\n" +
                    "                    cubicInterpolationMode: 'monotone',\n" +
                    "                    tension: 0.4\n" +
                    "                  },\n" +
                    "                  {\n" +
                    "                    label: \"Mean\",\n" +
                    "                    fill: false,\n" +
                    "                    backgroundColor: shadeColor(getColor(), 25),\n" +
                    "                    borderColor: getColor(),\n" +
                    "                    data: [1000],\n" +
                    "                    cubicInterpolationMode: 'monotone',\n" +
                    "                    tension: 0.4\n" +
                    "                  }\n" +
                    "                ]")
        }
    }

}
