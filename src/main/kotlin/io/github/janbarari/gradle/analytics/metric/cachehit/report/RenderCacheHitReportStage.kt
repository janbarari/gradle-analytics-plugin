package io.github.janbarari.gradle.analytics.metric.cachehit.report

import io.github.janbarari.gradle.analytics.domain.model.ModuleCacheHitReport
import io.github.janbarari.gradle.analytics.domain.model.Report
import io.github.janbarari.gradle.core.Stage
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.getTextResourceContent
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.removeLastChar
import io.github.janbarari.gradle.extension.toIntList
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull

class RenderCacheHitReportStage(
    private val report: Report
) : Stage<String, String> {

    @Suppress("LongMethod")
    override suspend fun process(input: String): String {
        if (report.cacheHitReport.isNull()) return input.replace(
            "%cache-hit-metric%",
            "<p>Cache Hit Metric is not available</p><div class\"space\"></div>"
        )

        val overallValues = ensureNotNull(report.cacheHitReport)
            .overallValues
            .map { it.value }
            .toIntList()
            .toString()

        val overallLabels = StringBuilder()
        ensureNotNull(report.cacheHitReport)
            .overallValues
            .map { it.description }
            .whenEach {
                overallLabels.append("\"$this\"").append(",")
            }.also {
                //because the last item should not have ',' separator.
                overallLabels.removeLastChar()
            }

        val tableData = buildString {
            report.cacheHitReport?.modules?.forEachIndexed { index, it ->
                var diffRatio = "<td>-</td>"
                it.diffRatio.whenNotNull {
                    diffRatio = if (this > 0) {
                        "<td class=\"green\">+${this}%</td>"
                    } else if (this < 0) {
                        "<td class=\"red\">${this}%</td>"
                    } else {
                        "<td>Equals</td>"
                    }
                }
                append(
                    """
                    <tr>
                        <td>${index + 1}</td>
                        <td>${it.path}</td>
                        <td>${it.hitRatio}%</td>
                        $diffRatio
                    </tr>
                """.trimIndent()
                )
            }
        }

        val overallCacheHit = ensureNotNull(report.cacheHitReport).overallHit.toString() + "%"

        var overallDiffRatio = "<td>-</td>"
        ensureNotNull(report.cacheHitReport).overallDiffRatio.whenNotNull {
            overallDiffRatio = if (this > 0) {
                "<td class=\"green\">+${this}%</td>"
            } else if (this < 0) {
                "<td class=\"red\">${this}%</td>"
            } else {
                "<td>Equals</td>"
            }
        }

        val bestModulePath = getBestModulePath(ensureNotNull(report.cacheHitReport).modules)
        val worstModulePath = getWorstModulePath(ensureNotNull(report.cacheHitReport).modules)

        val bestValues = ensureNotNull(report.cacheHitReport).modules
            .first { it.path == bestModulePath }
            .values
            .map {
                it.value
            }
            .toIntList()
            .toString()

        val worstValues = ensureNotNull(report.cacheHitReport).modules
            .first { it.path == worstModulePath }
            .values
            .map {
                it.value
            }
            .toIntList()
            .toString()

        val bwLabels = StringBuilder()
        ensureNotNull(report.cacheHitReport).modules
            .first { it.path == worstModulePath }
            .values
            .map { it.description }
            .whenEach {
                bwLabels.append("\"$this\"").append(",")
            }.also {
                //because the last item should not have ',' separator.
                bwLabels.removeLastChar()
            }

        var template = getTextResourceContent("cache-hit-metric-template.html")
        template = template.replace("%overall-values%", overallValues)
            .replace("%overall-labels%", overallLabels.toString())
            .replace("%table-data%", tableData)
            .replace("%overall-cache-hit%", overallCacheHit)
            .replace("%overall-diff-ratio%", overallDiffRatio)
            .replace("%best-values%", bestValues)
            .replace("%worst-values%", worstValues)
            .replace("%bw-labels%", bwLabels.toString())
            .replace("%worst-module-name%", "\"$worstModulePath\"")
            .replace("%best-module-name%", "\"$bestModulePath\"")

        return input.replace("%cache-hit-metric%", template)
    }

    private fun getBestModulePath(modules: List<ModuleCacheHitReport>): String? {
        if (modules.isEmpty()) return null
        return modules.sortedByDescending { module ->
            module.values.sumOf { it.value }
        }.first().path
    }

    private fun getWorstModulePath(modules: List<ModuleCacheHitReport>): String? {
        if (modules.isNull()) return null
        return modules.sortedByDescending { module ->
            module.values.sumOf { it.value }
        }.last().path
    }

}
