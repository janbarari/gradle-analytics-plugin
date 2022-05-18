package io.github.janbarari.gradle.analytics.data.repository

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.janbarari.gradle.analytics.data.database.Database
import io.github.janbarari.gradle.analytics.data.database.table.MetricTable
import io.github.janbarari.gradle.analytics.data.database.table.TemporaryMetricTable
import io.github.janbarari.gradle.analytics.data.database.table.TemporaryMetricTable.value
import io.github.janbarari.gradle.analytics.domain.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.utils.Clock
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere

class DatabaseRepositoryImp(private val db: Database): DatabaseRepository {

    private var moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private var jsonAdapter: JsonAdapter<BuildMetric> = moshi.adapter(BuildMetric::class.java)

    override fun saveNewMetric(metric: BuildMetric): Boolean {
        return db.transaction {
            val queryResult = MetricTable.insert {
                it[createdAt] = System.currentTimeMillis()
                it[value] = jsonAdapter.toJson(metric)
            }
            return@transaction queryResult.insertedCount == 1
        }
    }

    override fun saveTemporaryMetric(metric: BuildMetric): Boolean {
        return db.transaction {
            val queryResult = TemporaryMetricTable.insert {
                it[createdAt] = System.currentTimeMillis()
                it[value] = jsonAdapter.toJson(metric)
            }
            return@transaction queryResult.insertedCount == 1
        }
    }

    override fun getDayMetric(): Pair<BuildMetric, Long> {
        return db.transaction {
            val queryResult = MetricTable.select {
                (MetricTable.createdAt greaterEq Clock.getDayStartMs()) and
                        (MetricTable.createdAt less Clock.getDayEndMs())
            }.single()
            return@transaction Pair(
                jsonAdapter.fromJson(queryResult[MetricTable.value])!!,
                queryResult[MetricTable.number]
            )
        }
    }

    override fun isDayMetricExists(): Boolean {
        return db.transaction {
            val queryResult = MetricTable.select {
                (MetricTable.createdAt greaterEq Clock.getDayStartMs()) and
                        (MetricTable.createdAt less Clock.getDayEndMs())
            }
            return@transaction queryResult.count() > 0
        }
    }

    override fun updateDayMetric(number: Long, metric: BuildMetric): Boolean {
        return db.transaction {
            val queryResult = MetricTable.update({
                MetricTable.number eq number
            }) {
                it[value] = jsonAdapter.toJson(metric)
                it[createdAt] = System.currentTimeMillis()
            }
            return@transaction queryResult == 1
        }
    }

    override fun dropOutdatedTemporaryMetrics(): Boolean {
        return db.transaction {
            val queryResult = TemporaryMetricTable.deleteWhere {
                TemporaryMetricTable.createdAt less Clock.getDayStartMs()
            }
            return@transaction queryResult > 0
        }
    }

    override fun getTemporaryMetrics(): List<BuildMetric> {
        return db.transaction {
            val metrics = arrayListOf<BuildMetric>()
            val queryResult = TemporaryMetricTable.selectAll()
            queryResult.toList().forEach {
                jsonAdapter.fromJson(it[value])?.let { metric ->
                    metrics.add(metric)
                }
            }
            return@transaction metrics
        }
    }

}
