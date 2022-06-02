package io.github.janbarari.gradle.analytics.domain.repository

import io.github.janbarari.gradle.analytics.domain.model.BuildMetric

interface DatabaseRepository {
    fun saveNewMetric(metric: BuildMetric): Long
    fun saveTemporaryMetric(metric: BuildMetric): Long

    fun isDayMetricExists(): Boolean

    fun getDayMetric(): Pair<BuildMetric, Long>
    fun getMetrics(period: Long): List<BuildMetric>
    fun getTemporaryMetrics(): List<BuildMetric>

    fun updateDayMetric(number: Long, metric: BuildMetric): Boolean

    fun dropOutdatedTemporaryMetrics(): Boolean
    fun dropMetrics(): Boolean
}
