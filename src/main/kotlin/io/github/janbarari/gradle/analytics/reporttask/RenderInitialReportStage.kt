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
package io.github.janbarari.gradle.analytics.reporttask

import io.github.janbarari.gradle.analytics.GradleAnalyticsPlugin
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.utils.DateTimeUtils

class RenderInitialReportStage private constructor(
    private val data: List<BuildMetric>,
    private val projectName: String,
    private val requestedTasks: String,
    private val branch: String,
    private val period: Long,
    private val isCI: Boolean
) : Stage<String, String> {

    class Builder {

        private var data: List<BuildMetric>? = null
        private var projectName: String? = null
        private var requestedTasks: String? = null
        private var branch: String? = null
        private var period: Long? = null
        private var isCI: Boolean? = null

        fun data(value: List<BuildMetric>) = apply {
            data = value
        }

        fun projectName(value: String) = apply {
            projectName = value
        }

        fun requestedTasks(value: String) = apply {
            requestedTasks = value
        }

        fun branch(value: String) = apply {
            branch = value
        }

        fun period(value: Long) = apply {
            period = value
        }

        fun isCI(value: Boolean) = apply {
            isCI = value
        }

        fun build(): RenderInitialReportStage {
            return RenderInitialReportStage(
                data!!,
                projectName!!,
                requestedTasks!!,
                branch!!,
                period!!,
                isCI!!
            )
        }

    }

    override suspend fun process(input: String): String {
        var result = input.replace("%root-project-name%", projectName)
            .replace("%task-path%", requestedTasks)
            .replace("%branch%", branch)
            .replace("%time-period-title%", "$period Months")
            .replace("%reported-at%", DateTimeUtils.formatToDateTime(System.currentTimeMillis()))
            .replace("%is-ci%", if (isCI) "Yes" else "No")
            .replace("%plugin-version%", GradleAnalyticsPlugin.PLUGIN_VERSION)

        if (data.isNotEmpty()) {
            val oldest = data.last()
            val newest = data.first()
            result = result.replace("%time-period-end%", DateTimeUtils.formatToDate(oldest.createdAt))
                .replace("%time-period-start%", DateTimeUtils.formatToDate(newest.createdAt))
        }

        return result
    }

}
