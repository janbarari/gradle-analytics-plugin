package io.github.janbarari.gradle.analytics.metric.totalbuild

import io.github.janbarari.gradle.analytics.domain.model.TotalBuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.isBiggerEquals
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.extension.whenTrue
import io.github.janbarari.gradle.utils.MathUtils

class UpdateTotalBuildMetricUseCase(
    private val repo: DatabaseRepository
) : UseCaseNoInput<TotalBuildMetric>() {

    @Suppress("MagicNumber")
    override fun execute(): TotalBuildMetric {
        val durations = arrayListOf<Long>()

        repo.getTemporaryMetrics().whenEach {
            totalBuildMetric.whenNotNull {
                average.isBiggerEquals(50).whenTrue {
                    durations.add(average)
                }
            }
        }

        return TotalBuildMetric(
            average = MathUtils.longMedian(durations)
        )
    }

}
