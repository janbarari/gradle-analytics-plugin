/**
 * MIT License
 * Copyright (c) 2022 Mehdi Janbarari (@janbarari)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.create

import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.analytics.domain.model.metric.ParallelExecutionRateMetric
import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.extension.toPercentageOf
import io.github.janbarari.gradle.extension.whenNotNull

class CreateParallelExecutionRateMetricUseCase : UseCase<BuildInfo, ParallelExecutionRateMetric>() {

    override suspend fun execute(input: BuildInfo): ParallelExecutionRateMetric {
        val nonParallelExecutionInMillis = calculateNonParallelExecutionDuration(input.executedTasks)
        val parallelExecutionInMillis = calculateParallelExecutionInMillis(input.executedTasks)
        val rate = (parallelExecutionInMillis - nonParallelExecutionInMillis)
            .toPercentageOf(nonParallelExecutionInMillis)
            .toLong()

        return ParallelExecutionRateMetric(medianRate = rate)
    }

    /**
     * Gradle executes the project tasks in parallel to use maximum performance of
     * the system resources, Which means by adding the task's duration together,
     * We calculated the serial duration. to calculate the non-parallel
     * duration(real-life duration) we need to ignore those tasks that are executed at
     * the same time or covered times by another task.
     */
    @Suppress("NestedBlockDepth", "ComplexMethod")
    fun calculateNonParallelExecutionDuration(executedTasks: Collection<TaskInfo>): Long {
        fun checkIfCanMerge(
            parallelTask: TaskInfo,
            nonParallelTask: Map.Entry<Int, Pair<Long, Long>>,
            nonParallelTasks: HashMap<Int, Pair<Long, Long>>
        ) {
            if (parallelTask.startedAt <= nonParallelTask.value.second &&
                parallelTask.finishedAt >= nonParallelTask.value.second
            ) {
                var start = nonParallelTask.value.first
                var end = nonParallelTask.value.second
                if (parallelTask.startedAt < nonParallelTask.value.first) {
                    start = parallelTask.startedAt
                }
                if (parallelTask.finishedAt > nonParallelTask.value.second) {
                    end = parallelTask.finishedAt
                }
                nonParallelTasks[nonParallelTask.key] = Pair(start, end)
            }
        }

        val nonParallelDurations = hashMapOf<Int, Pair<Long, Long>>()

        val executedTaskIterator = executedTasks
            .sortedBy { task -> task.startedAt }
            .iterator()

        while (executedTaskIterator.hasNext()) {
            val executedTask = executedTaskIterator.next()
            if (nonParallelDurations.isEmpty()) {
                nonParallelDurations[nonParallelDurations.size] = Pair(executedTask.startedAt, executedTask.finishedAt)
                continue
            }

            var tempTask: Pair<Long, Long>? = null
            val nonParallelTasksIterator = nonParallelDurations.iterator()
            while (nonParallelTasksIterator.hasNext()) {
                val nonParallelTask = nonParallelTasksIterator.next()

                checkIfCanMerge(executedTask, nonParallelTask, nonParallelDurations)

                if (executedTask.startedAt > nonParallelTask.value.first &&
                    executedTask.finishedAt > nonParallelTask.value.second &&
                    executedTask.finishedAt > executedTask.startedAt) {
                    tempTask = Pair(executedTask.startedAt, executedTask.finishedAt)
                }
            }

            tempTask.whenNotNull {
                val iterator = nonParallelDurations.iterator()
                while (iterator.hasNext()) {
                    val nonParallelTask = iterator.next()
                    if (nonParallelTask.value.second in first..second) {
                        var start = nonParallelTask.value.first
                        var end = nonParallelTask.value.second
                        if (first < nonParallelTask.value.first) {
                            start = first
                        }
                        if (second > nonParallelTask.value.second) {
                            end = second
                        }
                        nonParallelDurations[nonParallelTask.key] = Pair(start, end)
                    }
                }

                val biggestNonParallelTaskEnd = nonParallelDurations.toList().maxByOrNull { it.second.second }!!.second.second
                if (first > biggestNonParallelTaskEnd) {
                    nonParallelDurations[nonParallelDurations.size] = this
                }
            }
        }

        return nonParallelDurations.toList().sumOf { (it.second.second - it.second.first) }
    }

    /**
     * Calculates the cumulative parallel duration.
     */
    fun calculateParallelExecutionInMillis(executedTasks: Collection<TaskInfo>): Long {
        return executedTasks.sumOf { it.getDurationInMillis() }
    }

}
