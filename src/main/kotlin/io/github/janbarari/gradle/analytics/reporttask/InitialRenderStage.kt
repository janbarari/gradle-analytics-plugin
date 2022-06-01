package io.github.janbarari.gradle.analytics.reporttask

import io.github.janbarari.gradle.analytics.GradleAnalyticsPlugin
import io.github.janbarari.gradle.analytics.core.Stage
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.utils.DateTimeUtils

class InitialRenderStage private constructor(
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

        fun build(): InitialRenderStage {
            return InitialRenderStage(
                data!!,
                projectName!!,
                requestedTasks!!,
                branch!!,
                period!!,
                isCI!!
            )
        }

    }

    override fun process(input: String): String {
        var result = input.replace("%root-project-name%", projectName)
            .replace("%task-path%", requestedTasks)
            .replace("%branch%", branch)
            .replace("%time-period-title%", "$period Months")
            .replace("%reported-at%", DateTimeUtils.msToDateTimeString(System.currentTimeMillis()))
            .replace("%is-ci%", if (isCI) "Yes" else "No")
            .replace("%plugin-version%", GradleAnalyticsPlugin.PLUGIN_VERSION)

        if (data.isNotEmpty()) {
            val oldest = data.last()
            val newest = data.first()
            result = result.replace("%time-period-start%", DateTimeUtils.msToDateString(oldest.createdAt))
                .replace("%time-period-end%", DateTimeUtils.msToDateString(newest.createdAt))
        }

        return result
    }

}
