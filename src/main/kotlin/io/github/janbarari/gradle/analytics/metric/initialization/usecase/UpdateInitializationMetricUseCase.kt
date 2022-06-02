package io.github.janbarari.gradle.analytics.metric.initialization.usecase

import io.github.janbarari.gradle.analytics.domain.model.InitializationMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.isBiggerEquals
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.extension.whenTrue
import io.github.janbarari.gradle.utils.MathUtils

class UpdateInitializationMetricUseCase(
    private val repo: DatabaseRepository
) : UseCaseNoInput<InitializationMetric>() {

    @Suppress("MagicNumber")
    override fun execute(): InitializationMetric {
        val durations = arrayListOf<Long>()

        repo.getTemporaryMetrics().whenEach {
            initializationMetric.whenNotNull {
                average.isBiggerEquals(50).whenTrue {
                    durations.add(average)
                }
            }
        }

        return InitializationMetric(
            average = MathUtils.longMedian(durations)
        )
    }

}
