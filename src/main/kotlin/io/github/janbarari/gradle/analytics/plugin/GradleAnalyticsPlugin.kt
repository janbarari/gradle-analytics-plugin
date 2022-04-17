package io.github.janbarari.gradle.analytics.plugin

import io.github.janbarari.gradle.analytics.core.build.BuildInfo
import io.github.janbarari.gradle.analytics.core.build.BuildObserver
import io.github.janbarari.gradle.analytics.core.ensurePluginCompatibleWithGradle
import io.github.janbarari.gradle.analytics.plugin.di.setupKoin
import io.github.janbarari.gradle.analytics.core.task.Task
import io.github.janbarari.gradle.analytics.core.task.TasksOperation
import io.github.janbarari.gradle.analytics.core.task.TasksOperationParams
import io.github.janbarari.gradle.bus.Bus
import io.github.janbarari.gradle.bus.Observer
import io.github.janbarari.gradle.os.OperatingSystem
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration
import javax.inject.Inject

@Suppress("UnstableApiUsage")
class GradleAnalyticsPlugin @Inject constructor(
    private val registry: BuildEventsListenerRegistry
) : Plugin<Project>, KoinComponent {

    private val linearTasks = hashMapOf<Int, Pair<Long, Long>>()
    private val operatingSystem by inject<OperatingSystem>()

    init {
        setupKoin()
    }

    override fun apply(project: Project) {
        println(operatingSystem.getManufacturer() + " " + operatingSystem.getType())
        println("cpu info, available processors: " + Runtime.getRuntime().availableProcessors())
        println("hardware info, free memory: " + Runtime.getRuntime().freeMemory())
        val sysInfo = oshi.SystemInfo()
        println("gpu details: " + sysInfo.hardware.graphicsCards[1].vendor)

        ensurePluginCompatibleWithGradle()
        initializeTasksOperationListener(project)
        BuildObserver.setup(onBuildFinishedListener = { buildInfo, duration ->
            println()
            println("BUILD PROCESS FINISHED in ${duration.seconds}s")
            println("%s TASKS EXECUTED".format(buildInfo.getTasksCount()))
            calculateTaskLinearTime(buildInfo)
        })
    }

    private fun initializeTasksOperationListener(project: Project) {
        val tasksOperationEventGUID = Observer.generateGUID()
        val tasksOperationListener = project.gradle.sharedServices.registerIfAbsent(
            TasksOperation::class.java.simpleName, TasksOperation::class.java
        ) { spec ->
            val params = TasksOperationParams(tasksOperationEventGUID)
            spec.parameters.getParams().set(params)
        }
        registry.onTaskCompletion(tasksOperationListener)
        Bus.register<Collection<Task>>(tasksOperationEventGUID) {
            BuildObserver.processFinished(BuildInfo(it))
        }
    }

    private fun printTotalParallelDuration(buildInfo: BuildInfo) {
        var totalParallelDurationMillis: Long = 0
        val iterator = buildInfo.executedTasks.iterator()
        while (iterator.hasNext()) {
            val executedTask = iterator.next()
            totalParallelDurationMillis += (executedTask.endTime - executedTask.startTime)
        }
        val totalParallelDurationSeconds = Duration.ofMillis(totalParallelDurationMillis).seconds
        println("TOTAL PARALLEL DURATION is ${totalParallelDurationSeconds}s")
    }

    private fun getSortedExecutedTasks(buildInfo: BuildInfo): Collection<Task> {
        return buildInfo.executedTasks.sortedBy { task -> task.startTime }
    }

    private fun printTotalLinearDuration() {
        var totalTasksLinearDurationMillis = 0L
        val linearTasksIterator = linearTasks.iterator()
        while (linearTasksIterator.hasNext()) {
            val linearTask = linearTasksIterator.next().value
            totalTasksLinearDurationMillis += (linearTask.second - linearTask.first)
            println("*** Linear task ::: [${linearTask.first}-${linearTask.second}]")
        }
        val totalTasksLinearDurationSeconds = Duration.ofMillis(totalTasksLinearDurationMillis).seconds
        println("TOTAL LINEAR DURATION is ${totalTasksLinearDurationSeconds}s")
    }

    private fun calculateTaskLinearTime(buildInfo: BuildInfo) {
        printTotalParallelDuration(buildInfo)

        val iterator = getSortedExecutedTasks(buildInfo).iterator()
        while (iterator.hasNext()) {

            val parallelTask = iterator.next()
            if (linearTasks.isEmpty()) {
                linearTasks[linearTasks.size] = Pair(parallelTask.startTime, parallelTask.endTime)
                continue
            }

            var shouldBeAddedItem: Pair<Long, Long>? = null
            val linearTasksIterator = linearTasks.iterator()

            while (linearTasksIterator.hasNext()) {
                val linearTask = linearTasksIterator.next()

                checkIfCanMerge(parallelTask, linearTask)

                if (parallelTask.startTime > linearTask.value.first &&
                    parallelTask.endTime > linearTask.value.second &&
                    parallelTask.endTime > parallelTask.startTime
                ) {
                    shouldBeAddedItem = Pair(parallelTask.startTime, parallelTask.endTime)
                }

            }

            shouldBeAddedItem?.let { new ->
                val itr = linearTasks.iterator()
                while (itr.hasNext()) {
                    val linearTask = itr.next()

                    if (new.first <= linearTask.value.second &&
                        new.second >= linearTask.value.second
                    ) {
                        var start = linearTask.value.first
                        var end = linearTask.value.second
                        if (new.first < linearTask.value.first) {
                            start = new.first
                        }
                        if (new.second > linearTask.value.second) {
                            end = new.second
                        }
                        linearTasks[linearTask.key] = Pair(start, end)

                    }
                }

                val biggestLTEnd = linearTasks.toList().maxByOrNull { it.second.second }!!.second.second
                if (new.first > biggestLTEnd) {
                    linearTasks[linearTasks.size] = new
                }

            }

        }

        printTotalLinearDuration()

    }

    private fun checkIfCanMerge(parallelTask: Task, linearTask: Map.Entry<Int, Pair<Long, Long>>) {
        if (parallelTask.startTime <= linearTask.value.second && parallelTask.endTime >= linearTask.value.second) {
            var start = linearTask.value.first
            var end = linearTask.value.second
            if (parallelTask.startTime < linearTask.value.first) {
                start = parallelTask.startTime
            }
            if (parallelTask.endTime > linearTask.value.second) {
                end = parallelTask.endTime
            }
            linearTasks[linearTask.key] = Pair(start, end)
        }
    }

}
