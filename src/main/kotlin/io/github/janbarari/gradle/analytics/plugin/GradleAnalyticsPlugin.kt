package io.github.janbarari.gradle.analytics.plugin

import io.github.janbarari.gradle.analytics.core.BuildGlobalListener
import io.github.janbarari.gradle.analytics.core.BuildInfo
import io.github.janbarari.gradle.analytics.core.BuildTimeRecorder
import io.github.janbarari.gradle.analytics.core.TaskInfo
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry
import java.time.Duration
import javax.inject.Inject

@Suppress("UnstableApiUsage")
class GradleAnalyticsPlugin @Inject constructor(
    private val registry: BuildEventsListenerRegistry
) : Plugin<Project> {

    private val linearBuilds = hashMapOf<Int, Pair<Long, Long>>()

    override fun apply(project: Project) {
        var isBeforeEvaluationExecuted: Boolean = false
        project.gradle.beforeSettings {
            isBeforeEvaluationExecuted = true
        }
        BuildGlobalListener.listener = {
            parse(it)
            println("Is before evaluation executed: $isBeforeEvaluationExecuted")
        }
        val clazz = BuildTimeRecorder::class.java
        val timingRecorder = project.gradle.sharedServices.registerIfAbsent(clazz.simpleName, clazz) { }
        registry.onTaskCompletion(timingRecorder)
    }

    private fun parse(it: BuildInfo) {
        val buildDuration = Duration.ofMillis(it.endTime - it.startTime).seconds
        println("Execution Took: $buildDuration seconds")
        var totalUnrealTasksDuration = 0L
        it.tasks.forEach {
            totalUnrealTasksDuration += it.endTime - it.startTime
        }
        totalUnrealTasksDuration = Duration.ofMillis(totalUnrealTasksDuration).seconds
        println("Total Unreal time is $totalUnrealTasksDuration seconds")
        val sortedParallelBuilds = it.tasks.sortedBy { task -> task.startTime }
        sortedParallelBuilds.forEach af@{ parallelTask ->
            if (linearBuilds.isEmpty()) {
                linearBuilds[linearBuilds.size] = Pair(parallelTask.startTime, parallelTask.endTime)
                return@af
            }
            var shouldBeAddedItem: Pair<Long, Long>? = null

            linearBuilds.forEach { linearTask ->
                checkIfCanMerge(parallelTask, linearTask)
                if (parallelTask.startTime > linearTask.value.first && parallelTask.endTime > linearTask.value.second) {
                    shouldBeAddedItem = Pair(parallelTask.startTime, parallelTask.endTime)
                }
            }

            if (shouldBeAddedItem != null) {
                linearBuilds[linearBuilds.size] = shouldBeAddedItem!!
            }
        }
        var realtime = 0L
        linearBuilds.forEach {
            realtime += (it.value.second - it.value.first)
        }
        val realtimeS = Duration.ofMillis(realtime).seconds
        println("Real value is: " + realtimeS + " seconds")
    }

    private fun checkIfCanMerge(parallelTask: TaskInfo, linearTask: Map.Entry<Int, Pair<Long, Long>>) {
        if (parallelTask.startTime <= linearTask.value.second && parallelTask.endTime >= linearTask.value.second) {
            var start = linearTask.value.first
            var end = linearTask.value.second
            if (parallelTask.startTime < linearTask.value.first) {
                start = parallelTask.endTime
            }
            if (parallelTask.endTime > linearTask.value.second) {
                end = parallelTask.endTime
            }
            linearBuilds[linearTask.key] = Pair(start, end)
        }
    }

}
