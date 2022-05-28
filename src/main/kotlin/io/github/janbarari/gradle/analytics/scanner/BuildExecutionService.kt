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
package io.github.janbarari.gradle.analytics.scanner

import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import io.github.janbarari.gradle.analytics.data.database.Database
import io.github.janbarari.gradle.analytics.data.DatabaseRepositoryImp
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.initializationmetric.InitializationMetricMedianUseCase
import io.github.janbarari.gradle.analytics.initializationmetric.InitializationMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.SaveMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.SaveTemporaryMetricUseCase
import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.analytics.domain.model.OsInfo
import io.github.janbarari.gradle.analytics.domain.model.HardwareInfo
import io.github.janbarari.gradle.analytics.domain.model.DependencyResolveInfo
import io.github.janbarari.gradle.os.OperatingSystemImp
import io.github.janbarari.gradle.utils.GitUtils
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.separateElementsWithSpace
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.task.TaskFinishEvent
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Tracks the task's execution information.
 *
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
abstract class BuildExecutionService : BuildService<BuildExecutionService.Params>, OperationCompletionListener,
    AutoCloseable {

    interface Params : BuildServiceParameters {
        val databaseConfig: Property<GradleAnalyticsPluginConfig.DatabaseConfig>
        val envCI: Property<Boolean>
        val requestedTasks: ListProperty<String>
        val trackingTasks: ListProperty<String>
        val trackingBranches: ListProperty<String>
    }

    private val executedTasks: ConcurrentLinkedQueue<TaskInfo> = ConcurrentLinkedQueue()

    init {
        assignStartTimestampIfProcessSkipped()
        assignInitializationTimestampIfProcessSkipped()
        assignConfigurationTimestampIfProcessSkipped()
    }

    /**
     * If the build initialization is reused by configuration-cache, then the
     * [BuildInitializationService] won't  register to the project and the start time won't assign.
     */
    private fun assignStartTimestampIfProcessSkipped() {
        if (BuildInitializationService.STARTED_AT == 0L) {
            BuildInitializationService.STARTED_AT = System.currentTimeMillis()
        }
    }

    /**
     * If the build initialization is reused by configuration-cache, then the
     * [BuildInitializationService] won't  register to the project and the initialization time won't assign.
     */
    private fun assignInitializationTimestampIfProcessSkipped() {
        if (BuildInitializationService.INITIALIZED_AT == 0L) {
            BuildInitializationService.INITIALIZED_AT = System.currentTimeMillis()
        }
    }

    /**
     * If the build configuration is reused by configuration-cache, then the
     * [BuildConfigurationService] won't  register to the project and the configuration time won't assign.
     */
    private fun assignConfigurationTimestampIfProcessSkipped() {
        if (BuildConfigurationService.CONFIGURED_AT == 0L) {
            BuildConfigurationService.CONFIGURED_AT = System.currentTimeMillis()
        }
    }

    /**
     * Called when each task execution is finished.
     * @param event Task finish event
     */
    override fun onFinish(event: FinishEvent?) {
        if (event is TaskFinishEvent) {
            executedTasks.add(
                TaskInfo(
                    event.result.startTime,
                    event.result.endTime,
                    event.descriptor.taskPath,
                    event.descriptor.displayName,
                    event.descriptor.name
                )
            )
        }
    }

    /**
     * Called when the build execution process finished.
     */
    override fun close() {
        onExecutionFinished(executedTasks)
        executedTasks.clear()
    }

    private fun isCiProcessNotTrackable(): Boolean {
        return parameters.databaseConfig.get().ci.isNull() && parameters.envCI.get() == true
    }

    private fun isLocalProcessNotTrackable(): Boolean {
        return parameters.envCI.get() == false && parameters.databaseConfig.get().local.isNull()
    }

    @Suppress("ReturnCount")
    private fun onExecutionFinished(tasks: Collection<TaskInfo>) {
        if (isForbiddenTasksRequested(parameters.requestedTasks.get())) return

        if (isCiProcessNotTrackable() || isLocalProcessNotTrackable()) {
            println("Not Tracked since CI database configuration is not defined for CI machine")
            return
        }

        if (isProcessTrackable().not() || isBranchTrackable().not()) {
            println("Process not trackable")
            return
        }

        val startedAt = BuildInitializationService.STARTED_AT
        val initializedAt = BuildInitializationService.INITIALIZED_AT
        val configuredAt = BuildConfigurationService.CONFIGURED_AT
        val finishedAt = System.currentTimeMillis()

        val dependenciesResolveInfo: ArrayList<DependencyResolveInfo> = arrayListOf()
        val iterator = BuildDependencyResolutionService.dependenciesResolveInfo.iterator()
        while (iterator.hasNext()) {
            val info = iterator.next()
            dependenciesResolveInfo.add(info.value)
        }

        BuildInitializationService.reset()
        BuildConfigurationService.reset()
        BuildDependencyResolutionService.reset()

        val osInfo = OsInfo(OperatingSystemImp().getName())

        val hardwareInfo = HardwareInfo(0, 0)

        val info = BuildInfo(
            startedAt, initializedAt, configuredAt, dependenciesResolveInfo, tasks, finishedAt, osInfo, hardwareInfo
        )

        val database = Database(parameters.databaseConfig.get(), parameters.envCI.get())
        val repo: DatabaseRepository = DatabaseRepositoryImp(
            database,
            GitUtils.getCurrentBranch(),
            parameters.requestedTasks.get().separateElementsWithSpace()
        )
        val saveMetricUseCase = SaveMetricUseCase(
            repo, InitializationMetricMedianUseCase(repo)
        )
        val saveTemporaryUseCase = SaveTemporaryMetricUseCase(repo)

        val metric = BuildMetric(
            branch = GitUtils.getCurrentBranch(),
            requestedTasks = parameters.requestedTasks.get()
        )

        metric.initializationMetric = InitializationMetricUseCase().execute(
            info.getInitializationDuration().toMillis()
        )

        val result = saveTemporaryUseCase.execute(metric)
        if (result) saveMetricUseCase.execute(metric)
    }

    private fun isForbiddenTasksRequested(requestedTasks: List<String>): Boolean {
        return requestedTasks.contains("reportAnalytics")
    }

    private fun isProcessTrackable(): Boolean {
        var result = false

        val trackingTasksIterator = parameters.trackingTasks.get().iterator()
        val requestedTasksIterator = parameters.requestedTasks.get().iterator()

        while (trackingTasksIterator.hasNext()) {
            val trackingTask = trackingTasksIterator.next()

            var combinedRequestedTasks = ""
            while (requestedTasksIterator.hasNext()) {
                val requestedTask = requestedTasksIterator.next()
                combinedRequestedTasks += requestedTask
            }

            if(trackingTask == combinedRequestedTasks) result = true
        }

        return result
    }

    private fun isBranchTrackable(): Boolean {
        var result = false
        val trackingBranchesIterator = parameters.trackingBranches.get().iterator()
        val currentBranch = GitUtils.getCurrentBranch()
        while (trackingBranchesIterator.hasNext()) {
            val trackingBranch = trackingBranchesIterator.next()
            if (currentBranch == trackingBranch) {
                result = true
            }
        }
        return result
    }

}
