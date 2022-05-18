package io.github.janbarari.gradle.analytics.domain.repository

import io.github.janbarari.gradle.analytics.domain.metric.BuildMetric

interface DatabaseRepository {
    fun saveNewMetric(metric: BuildMetric): Boolean
    fun saveTemporaryMetric(metric: BuildMetric): Boolean

    fun getDayMetric(): Pair<BuildMetric, Long>
    fun isDayMetricExists(): Boolean

    fun updateDayMetric(number: Long, metric: BuildMetric): Boolean

    fun dropOutdatedTemporaryMetrics(): Boolean
    fun getTemporaryMetrics(): List<BuildMetric>
}
