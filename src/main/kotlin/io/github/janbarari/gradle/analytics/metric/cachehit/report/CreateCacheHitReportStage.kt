package io.github.janbarari.gradle.analytics.metric.cachehit.report

import io.github.janbarari.gradle.analytics.CHART_MAX_COLUMNS
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.CacheHitReport
import io.github.janbarari.gradle.analytics.domain.model.ChartPoint
import io.github.janbarari.gradle.analytics.domain.model.ModuleCacheHitReport
import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.analytics.domain.model.TimespanChartPoint
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.diffPercentageOf
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.hasMultipleItems
import io.github.janbarari.gradle.extension.hasSingleItem
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.utils.DatasetUtils

@Suppress("UnusedPrivateMember")
class CreateCacheHitReportStage(
    private val metrics: List<BuildMetric>
) : Stage<Report, Report> {

    override suspend fun process(input: Report): Report {
        val metrics = metrics.filter {
            it.cacheHitMetric.isNotNull()
        }

        var result: CacheHitReport? = null

        if (metrics.isEmpty()) {
            result = null
        }

        if (metrics.hasSingleItem()) {
            result = generateSingleItemReport(metrics.single())
        }

        if (metrics.hasMultipleItems()) {
            result = generateMultipleItemsReport(metrics)
        }

        return input.apply {
            cacheHitReport = result
        }
    }

    private fun generateSingleItemReport(metric: BuildMetric): CacheHitReport {
        val modules = mutableListOf<ModuleCacheHitReport>()
        val overallHit = ensureNotNull(metric.cacheHitMetric).hitRatio

        val overallHitTimespanChartPoint = TimespanChartPoint(
            value = overallHit, from = metric.createdAt
        )

        val overallValues = listOf(
            ChartPoint(
                value = overallHitTimespanChartPoint.value,
                description = overallHitTimespanChartPoint.getTimespanString()
            )
        )

        ensureNotNull(metric.cacheHitMetric).modules.whenEach {
            val values = mutableListOf<ChartPoint>()
            TimespanChartPoint(
                value = hitRatio, from = metric.createdAt
            ).also {
                values.add(
                    ChartPoint(
                        value = it.value, description = it.getTimespanString()
                    )
                )
            }
            modules.add(
                ModuleCacheHitReport(
                    path = path, hitRatio = hitRatio, diffRatio = null, values = values
                )
            )
        }

        return CacheHitReport(
            modules = modules.sortedByDescending { it.hitRatio },
            overallHit = overallHit,
            overallDiffRatio = null,
            overallValues = overallValues
        )
    }

    private fun generateMultipleItemsReport(metrics: List<BuildMetric>): CacheHitReport {
        val firstCacheHitRatio = ensureNotNull(metrics.first().cacheHitMetric).hitRatio
        val lastCacheHitRatio = ensureNotNull(metrics.last().cacheHitMetric).hitRatio

        val overallDiffRatio = firstCacheHitRatio.diffPercentageOf(lastCacheHitRatio)

        val overallHit = ensureNotNull(metrics.last().cacheHitMetric).hitRatio

        val overallValuesTimestampChartPoints = mutableListOf<TimespanChartPoint>()
        metrics.whenEach {
            overallValuesTimestampChartPoints.add(
                TimespanChartPoint(
                    value = ensureNotNull(cacheHitMetric).hitRatio, from = createdAt
                )
            )
        }
        val minimizedOverallValues =
            DatasetUtils.minimizeTimespanChartPoints(overallValuesTimestampChartPoints, CHART_MAX_COLUMNS)
        val overallValues = minimizedOverallValues.map {
            ChartPoint(it.value, it.getTimespanString())
        }

        val modules = mutableListOf<ModuleCacheHitReport>()
        ensureNotNull(metrics.last().cacheHitMetric).modules.whenEach {
                modules.add(
                    ModuleCacheHitReport(
                        path = path,
                        hitRatio = hitRatio,
                        diffRatio = calculateModuleCacheHitDiffRatio(metrics, path, hitRatio),
                        values = getModuleChartPoints(path)
                    )
                )
            }

        return CacheHitReport(
            modules = modules,
            overallValues = overallValues,
            overallHit = overallHit,
            overallDiffRatio = overallDiffRatio
        )
    }

    private fun calculateModuleCacheHitDiffRatio(metrics: List<BuildMetric>, path: String, value: Long): Float? {
        return ensureNotNull(metrics.first().cacheHitMetric)
            .modules.find { it.path == path }?.hitRatio?.diffPercentageOf(value)
    }

    private fun getModuleChartPoints(path: String): List<ChartPoint> {
        val timestampChartPoints = mutableListOf<TimespanChartPoint>()
        metrics
            .filter {
                it.cacheHitMetric.isNotNull()
            }.whenEach {
                ensureNotNull(cacheHitMetric).modules.filter { it.path == path }.forEach {
                        timestampChartPoints.add(
                            TimespanChartPoint(
                                value = it.hitRatio, from = createdAt
                            )
                        )
                    }
            }
        val minimizedOverallValues = DatasetUtils.minimizeTimespanChartPoints(timestampChartPoints, CHART_MAX_COLUMNS)
        return minimizedOverallValues.map {
            ChartPoint(it.value, it.getTimespanString())
        }
    }
}
