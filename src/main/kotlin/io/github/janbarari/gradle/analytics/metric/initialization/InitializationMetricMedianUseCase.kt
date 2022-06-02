package io.github.janbarari.gradle.analytics.metric.initialization

import io.github.janbarari.gradle.analytics.core.UseCase
import io.github.janbarari.gradle.analytics.domain.model.InitializationMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.extension.isBigger
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.extension.whenTrue
import io.github.janbarari.gradle.utils.MathUtils

class InitializationMetricMedianUseCase(
    private val repo: DatabaseRepository
) : UseCase<Pair<String, List<String>>, InitializationMetric>() {

    @Suppress("MagicNumber")
    override fun execute(input: Pair<String, List<String>>): InitializationMetric {
        val durations = arrayListOf<Long>()

        repo.getTemporaryMetrics().whenEach {
            initializationMetric.whenNotNull {
                average.isBigger(50).whenTrue {
                    durations.add(average)
                }
            }
        }

        return InitializationMetric(
            average = MathUtils.longMedian(durations)
        )
    }

}
