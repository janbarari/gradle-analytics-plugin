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
package io.github.janbarari.gradle.analytics.domain.model

import io.github.janbarari.gradle.analytics.domain.model.os.HardwareInfo
import io.github.janbarari.gradle.analytics.domain.model.os.OsInfo
import io.github.janbarari.gradle.extension.whenNotNull
import org.gradle.tooling.Failure
import java.time.Duration

data class BuildInfo(
    val createdAt: Long,
    val startedAt: Long,
    val initializedAt: Long,
    val configuredAt: Long,
    var dependenciesResolveInfo: Collection<DependencyResolveInfo>,
    val executedTasks: List<TaskInfo>,
    val finishedAt: Long,
    val osInfo: OsInfo,
    val hardwareInfo: HardwareInfo,
    val branch: String,
    val gitHeadCommitHash: String,
    val requestedTasks: List<String>,
    val isSuccessful: Boolean,
    val failure: List<Failure>? = null
) : java.io.Serializable {

    init {
        // Replace pass-by-reference to pass-by-value, cause the collection will be reset after creation of BuildInfo.
        dependenciesResolveInfo = dependenciesResolveInfo.toList()
    }

    /**
     * Returns the total build duration.
     */
    fun getTotalDuration(): Duration {
        if (finishedAt < startedAt) return Duration.ofMillis(0)
        return Duration.ofMillis(finishedAt - startedAt)
    }

    /**
     * Returns the build initialization process duration.
     */
    fun getInitializationDuration(): Duration {
        if (initializedAt < startedAt) return Duration.ofMillis(0)
        return Duration.ofMillis(initializedAt - startedAt)
    }

    /**
     * Returns the build configuration process duration.
     */
    fun getConfigurationDuration(): Duration {
        if (configuredAt < initializedAt) return Duration.ofMillis(0)
        return Duration.ofMillis(configuredAt - initializedAt)
    }

    /**
     * Returns the build execution process duration.
     */
    fun getExecutionDuration(): Duration {
        if (finishedAt < configuredAt) return Duration.ofMillis(0)
        return Duration.ofMillis(finishedAt - configuredAt)
    }

    /**
     * Returns the total dependencies resolve duration.
     */
    fun getTotalDependenciesResolveDuration(): Duration {
        var result = 0L
        val iterator = dependenciesResolveInfo.iterator()
        while (iterator.hasNext()) {
            val info = iterator.next()
            result += info.getDuration()
        }
        return Duration.ofMillis(result)
    }

    /**
     * Calculates the cumulative parallel execution duration in milliseconds.
     */
    fun calculateParallelExecutionByMillis(): Long {
        return executedTasks.sumOf { it.getDurationByMillis() }
    }

    /**
     * Gradle executes the project tasks in parallel to use maximum performance of
     * the system resources, Which means by adding the task's duration together,
     * We calculated the serial duration. to calculate the non-parallel
     * duration(real-life duration) we need to ignore those tasks that are executed at
     * the same time or covered times by another task.
     */
    fun calculateNonParallelExecutionDuration(executedTasks: List<TaskInfo> = this.executedTasks): Long {
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

}
