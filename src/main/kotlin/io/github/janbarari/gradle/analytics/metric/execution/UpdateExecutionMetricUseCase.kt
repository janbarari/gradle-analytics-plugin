package io.github.janbarari.gradle.analytics.metric.execution

import io.github.janbarari.gradle.analytics.domain.model.ExecutionMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.core.UseCaseNoInput
import io.github.janbarari.gradle.extension.isBiggerEquals
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.extension.whenNotNull
import io.github.janbarari.gradle.extension.whenTrue
import io.github.janbarari.gradle.utils.MathUtils

class UpdateExecutionMetricUseCase(
    private val repo: DatabaseRepository
): UseCaseNoInput<ExecutionMetric>() {

    @Suppress("MagicNumber")
    override fun execute(): ExecutionMetric {
        val durations = arrayListOf<Long>()

        repo.getTemporaryMetrics().whenEach {
            executionMetric.whenNotNull {
                average.isBiggerEquals(50).whenTrue {
                    durations.add(average)
                }
            }
        }

        return ExecutionMetric(
            average = MathUtils.longMedian(durations)
        )
    }

}
