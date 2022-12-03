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

import io.github.janbarari.gradle.utils.MathUtils
import org.gradle.tooling.Failure
import java.time.Duration

data class BuildInfo(
    val createdAt: Long,
    val startedAt: Long,
    val initializedAt: Long,
    val configuredAt: Long,
    var dependenciesResolveInfo: List<DependencyResolveInfo>,
    val executedTasks: List<TaskInfo>,
    val finishedAt: Long,
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
        val mergeDependencyResolvesAndExecutionProcessTimeSlots: List<TimeSlot> = dependenciesResolveInfo.filter {
                it.finishedAt < configuredAt
            }.map {
                if (it.startedAt < configuredAt) {
                    TimeSlot(startedAt = configuredAt, finishedAt = it.finishedAt)
                } else {
                    TimeSlot(startedAt = it.startedAt, finishedAt = it.finishedAt)
                }
            } + executedTasks.map { TimeSlot(startedAt = it.startedAt, finishedAt = it.finishedAt) }

        // Find the remaining duration from the latest executed task till the finish process.
        var remainingDuration = 0L
        val lastExecutedTask = executedTasks.maxByOrNull { it.finishedAt }
        if (lastExecutedTask != null) {
            if (finishedAt > lastExecutedTask.finishedAt) {
                remainingDuration = finishedAt - lastExecutedTask.finishedAt
            }
        }

        return getInitializationDuration() + getConfigurationDuration() + Duration.ofMillis(
            MathUtils.calculateTimeSlotNonParallelDurationInMillis(
                mergeDependencyResolvesAndExecutionProcessTimeSlots
            )
        ) + Duration.ofMillis(remainingDuration)
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
        return Duration.ofMillis(calculateNonParallelExecutionInMillis(executedTasks))
    }

    /**
     * Returns the total dependencies resolve duration.
     */
    fun getTotalDependenciesResolveDuration(): Duration {
        return Duration.ofMillis(calculateNonParallelDependencyResolveInMillis(dependenciesResolveInfo))
    }

    /**
     * Calculates the cumulative parallel execution duration in milliseconds.
     */
    fun calculateParallelExecutionByMillis(): Long {
        return executedTasks.sumOf { it.getDurationInMillis() }
    }

    /**
     * Gradle executes the project tasks in parallel to use the maximum performance of
     * the system resources, Which means by adding the task's duration together,
     * We calculated the serial(parallel) duration. To calculate the non-parallel
     * duration(real-life duration) we need to ignore those tasks that are executed at
     * the same time or covered times by another task.
     */
    fun calculateNonParallelExecutionInMillis(
        executedTasks: List<TaskInfo> = this.executedTasks
    ): Long {
        return MathUtils.calculateTimeSlotNonParallelDurationInMillis(
            executedTasks.map { TimeSlot(startedAt = it.startedAt, finishedAt = it.finishedAt) }
        )
    }

    /**
     * Gradle downloads the project dependencies in parallel to use the maximum performance of
     * the system resource, Which means by adding the dependencies duration together,
     * We calculated the serial(parallel) duration. To calculate the non-parallel
     * duration(real-life duration) we need to ignore those tasks that are executed at
     * the same time or covered times by another time slot.
     */
    fun calculateNonParallelDependencyResolveInMillis(
        dependencyResolves: List<DependencyResolveInfo> = this.dependenciesResolveInfo
    ): Long {
        return MathUtils.calculateTimeSlotNonParallelDurationInMillis(
            dependencyResolves.map { TimeSlot(startedAt = it.startedAt, finishedAt = it.finishedAt) }
        )
    }

}
