package io.github.janbarari.gradle.analytics.plugin

import io.github.janbarari.gradle.analytics.core.BuildGlobalListener
import io.github.janbarari.gradle.analytics.core.BuildTimeRecorder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry
import java.time.Duration
import javax.inject.Inject

@Suppress("UnstableApiUsage")
class GradleAnalyticsPlugin @Inject constructor(private val registry: BuildEventsListenerRegistry) : Plugin<Project> {
    override fun apply(project: Project) {
        BuildGlobalListener.listener = {

            val buildDuration = Duration.ofMillis(it.endTime - it.startTime).seconds
            println("Execution Took: $buildDuration seconds")

            val linearBuilds = hashMapOf<Int, Pair<Long, Long>>()
            val sortedParallelBuilds = it.tasks.sortedBy { task -> task.startTime }
            sortedParallelBuilds.forEach { parallelTask ->
                if (linearBuilds.isEmpty()) {
                    linearBuilds[linearBuilds.size] = Pair(parallelTask.startTime, parallelTask.endTime)
                } else {
                    var shouldBeAddedItem: Pair<Long, Long>? = null

                    linearBuilds.forEach { linearTask ->
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
                        } else if (parallelTask.startTime > linearTask.value.first && parallelTask.endTime > linearTask.value.second) {
                            shouldBeAddedItem = Pair(parallelTask.startTime, parallelTask.endTime)
                        }
                    }

                    if (shouldBeAddedItem != null) {
                        linearBuilds[linearBuilds.size] = shouldBeAddedItem!!
                        shouldBeAddedItem = null
                    }
                }
            }

            println("================")
            var isTestFailed = false
            var previous = 0
            linearBuilds.toList().sortedBy { it.second.first }.forEach {
                if (it.second.first < previous) {
                    isTestFailed = true
                }
            }
            if (isTestFailed) {
                println("Test Failed")
            } else {
                println("Test Passed")
            }
            println("================")
            println(linearBuilds)
            println("================")

            var realtime = 0L
            linearBuilds.forEach {
                realtime += it.value.second - it.value.first
            }
            realtime = Duration.ofMillis(realtime).seconds
            println("Real value is: " + realtime + " seconds")

        }

        val clazz = BuildTimeRecorder::class.java
        val timingRecorder =
            project.gradle.sharedServices.registerIfAbsent(clazz.simpleName, clazz) {}

        registry.onTaskCompletion(timingRecorder)
        //todo gradle plugin apply function
        //Use publisher subscriber pattern to pass the class result to outside of the serialized class
    }
}
