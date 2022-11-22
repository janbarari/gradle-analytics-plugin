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
package io.github.janbarari.gradle.analytics.metric.configurationprocess.report

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.ConfigurationProcessMetric
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateConfigurationProcessReportStageTest {

    @Test
    fun `check process() generates report when report is not available`() = runBlocking {
        val metrics = mutableListOf<BuildMetric>()
        val stage = CreateConfigurationProcessReportStage(metrics)
        var report = Report("main", "assemble")
        report = stage.process(report)

        assertEquals(null, report.configurationProcessReport)
        assertEquals(null, report.configurationProcessReport)
    }

    @Test
    fun `check process() generates report when report is available`() = runBlocking {
        val metrics = mutableListOf<BuildMetric>()

        metrics.add(
            BuildMetric(
                branch = "main",
                requestedTasks = listOf("assemble"),
                createdAt = 1668836798265,
                gitHeadCommitHash = UUID.randomUUID().toString(),
                configurationProcessMetric = ConfigurationProcessMetric(
                    median = 1000L,
                    mean = 1200L
                )
            )
        )

        metrics.add(
            BuildMetric(
                branch = "main",
                requestedTasks = listOf("assemble"),
                createdAt = 1668936974389,
                gitHeadCommitHash = UUID.randomUUID().toString(),
                configurationProcessMetric = ConfigurationProcessMetric(
                    median = 900L,
                    mean = 1100L
                )
            )
        )

        val stage = CreateConfigurationProcessReportStage(metrics)
        var report = Report("main", "assemble")
        report = stage.process(report)

        assertEquals(2, report.configurationProcessReport!!.meanValues.size)
        assertEquals(2, report.configurationProcessReport!!.medianValues.size)

        assertTrue {
            report.configurationProcessReport!!.medianValues[0].value == 1000L &&
                    report.configurationProcessReport!!.medianValues[0].from == 1668836798265
        }
        assertTrue {
            report.configurationProcessReport!!.medianValues[1].value == 900L &&
                    report.configurationProcessReport!!.medianValues[1].from == 1668936974389
        }

        assertTrue {
            report.configurationProcessReport!!.meanValues[0].value == 1200L &&
                    report.configurationProcessReport!!.meanValues[0].from == 1668836798265
        }
        assertTrue {
            report.configurationProcessReport!!.meanValues[1].value == 1100L &&
                    report.configurationProcessReport!!.meanValues[1].from == 1668936974389
        }

    }

}
