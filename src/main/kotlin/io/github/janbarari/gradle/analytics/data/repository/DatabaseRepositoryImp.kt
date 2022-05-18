package io.github.janbarari.gradle.analytics.data.repository

import io.github.janbarari.gradle.analytics.data.database.Database
import io.github.janbarari.gradle.analytics.domain.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository

class DatabaseRepositoryImp(private val db: Database): DatabaseRepository {

    override fun saveNewMetric(metric: BuildMetric): Boolean {
        return db.saveNewMetric(metric)
    }

    override fun getTodayMetric(): Pair<BuildMetric, Long>? {
        return db.getTodayMetric()
    }

    override fun isTodayMetricExists(): Boolean {
        return db.isTodayMetricExists()
    }

    override fun updateExistingMetric(number: Long, metric: BuildMetric): Boolean {
        return db.updateExistingMetric(number, metric)
    }

}
