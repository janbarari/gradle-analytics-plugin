package io.github.janbarari.gradle.analytics.domain.repository

import io.github.janbarari.gradle.analytics.domain.metric.BuildMetric

interface DatabaseRepository {
    fun saveNewMetric(metric: BuildMetric): Boolean
    fun getTodayMetric(): Pair<BuildMetric, Long>?
    fun isTodayMetricExists(): Boolean
    fun updateExistingMetric(number: Long, metric: BuildMetric): Boolean
}