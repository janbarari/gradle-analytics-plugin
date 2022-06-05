package io.github.janbarari.gradle.analytics.metric.configuration

import io.github.janbarari.gradle.analytics.domain.model.ConfigurationMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.isBiggerEquals
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.extension.whenTrue
import io.github.janbarari.gradle.utils.MathUtils

class UpdateConfigurationMetricUseCase(
    private val repo: DatabaseRepository
): UseCaseNoInput<ConfigurationMetric>() {

    @Suppress("MagicNumber")
    override fun execute(): ConfigurationMetric {
        val durations = arrayListOf<Long>()

        repo.getTemporaryMetrics().whenEach {
            configurationMetric.whenNotNull {
                average.isBiggerEquals(50).whenTrue {
                    durations.add(average)
                }
            }
        }

        return ConfigurationMetric(
            average = MathUtils.longMedian(durations)
        )
    }
}
