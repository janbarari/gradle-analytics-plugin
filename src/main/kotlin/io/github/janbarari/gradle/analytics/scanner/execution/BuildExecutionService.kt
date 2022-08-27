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
package io.github.janbarari.gradle.analytics.scanner.execution

import io.github.janbarari.gradle.ExcludeJacocoGenerated
import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.model.ModulesDependencyGraph
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.analytics.scanner.configuration.BuildConfigurationService
import io.github.janbarari.gradle.analytics.scanner.initialization.BuildInitializationService
import io.github.janbarari.gradle.utils.GitUtils
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.Failure
import org.gradle.tooling.events.FailureResult
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.OperationDescriptor
import org.gradle.tooling.events.SkippedResult
import org.gradle.tooling.events.SuccessResult
import org.gradle.tooling.events.task.TaskFailureResult
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskSuccessResult
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Tracks the task's execution information.
 */
@ExcludeJacocoGenerated
abstract class BuildExecutionService : BuildService<BuildExecutionService.Params>, OperationCompletionListener, AutoCloseable {

    interface Params : BuildServiceParameters {
        val databaseConfig: Property<GradleAnalyticsPluginConfig.DatabaseConfig>
        val envCI: Property<Boolean>
        val requestedTasks: ListProperty<String>
        val trackingTasks: ListProperty<String>
        val trackingBranches: ListProperty<String>
        val modulesPath: ListProperty<ModulePath>
        val modulesDependencyGraph: Property<ModulesDependencyGraph>
    }

    private val executedTasks: ConcurrentLinkedQueue<TaskInfo> = ConcurrentLinkedQueue()

    init {
        assignStartTimestampIfProcessSkipped()
        assignInitializationTimestampIfProcessSkipped()
        assignConfigurationTimestampIfProcessSkipped()
    }

    /**
     * If the build initialization is reused by configuration-cache, then the
     * [io.github.janbarari.gradle.analytics.scanner.initialization.BuildInitializationService] won't
     * register to the project and the start time won't assign.
     */
    private fun assignStartTimestampIfProcessSkipped() {
        if (BuildInitializationService.STARTED_AT == 0L) {
            BuildInitializationService.STARTED_AT = System.currentTimeMillis()
        }
    }

    /**
     * If the build initialization is reused by configuration-cache, then the
     * [io.github.janbarari.gradle.analytics.scanner.initialization.BuildInitializationService] won't
     * register to the project and the initialization time won't assign.
     */
    private fun assignInitializationTimestampIfProcessSkipped() {
        if (BuildInitializationService.INITIALIZED_AT == 0L) {
            BuildInitializationService.INITIALIZED_AT = System.currentTimeMillis()
        }
    }

    /**
     * If the build configuration is reused by configuration-cache, then the
     * [io.github.janbarari.gradle.analytics.scanner.configuration.BuildConfigurationService] won't
     * register to the project and the configuration time won't assign.
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
    @ExcludeJacocoGenerated
    override fun onFinish(event: FinishEvent?) {
        if (event is TaskFinishEvent) {
            var dependencies: List<OperationDescriptor>? = null
            var isSuccessful = false
            var isSkipped = false
            var failures: List<Failure>? = null
            var isIncremental = false
            var isFromCache = false
            var isUpToDate = false
            var executionReasons: List<String>? = null

            runCatching {
                dependencies = event.descriptor.dependencies.toList()
            }

            when (event.result) {
                is FailureResult -> {
                    val result = event.result as TaskFailureResult
                    failures = result.failures
                    isIncremental = result.isIncremental
                    executionReasons = result.executionReasons
                }

                is SuccessResult -> {
                    isSuccessful = true
                    val result = event.result as TaskSuccessResult
                    isIncremental = result.isIncremental
                    isFromCache = result.isFromCache
                    isUpToDate = result.isUpToDate
                    executionReasons = result.executionReasons
                }

                is SkippedResult -> {
                    isSuccessful = true
                    isSkipped = true
                }
            }

            executedTasks.add(
                TaskInfo(
                    startedAt = event.result.startTime,
                    finishedAt = event.result.endTime,
                    path = event.descriptor.taskPath,
                    displayName = event.descriptor.displayName,
                    name = event.descriptor.name,
                    isSuccessful = isSuccessful,
                    failures = failures,
                    dependencies = dependencies,
                    isIncremental = isIncremental,
                    isFromCache = isFromCache,
                    isUpToDate = isUpToDate,
                    isSkipped = isSkipped,
                    executionReasons = executionReasons
                )
            )
        }
    }

    /**
     * Called once the build execution process finished.
     */
    @ExcludeJacocoGenerated
    override fun close() {
        BuildExecutionInjector(
            databaseConfig = parameters.databaseConfig.get(),
            isCI = parameters.envCI.get(),
            branch = GitUtils.currentBranch(),
            requestedTasks = parameters.requestedTasks.get(),
            trackingBranches = parameters.trackingBranches.get(),
            trackingTasks = parameters.trackingTasks.get(),
            modulesPath = parameters.modulesPath.get(),
            modulesDependencyGraph = parameters.modulesDependencyGraph.get()
        ).apply {
            provideBuildExecutionLogic().onExecutionFinished(executedTasks)
        }.also {
            executedTasks.clear()
        }
    }

}
