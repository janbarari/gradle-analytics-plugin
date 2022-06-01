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
import io.github.janbarari.gradle.analytics.data.DatabaseRepositoryImp
import io.github.janbarari.gradle.analytics.data.database.Database
import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.HardwareInfo
import io.github.janbarari.gradle.analytics.domain.model.OsInfo
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.domain.usecase.SaveMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.SaveTemporaryMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.InitializationMetricMedianUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.InitializationMetricUseCase
import io.github.janbarari.gradle.analytics.reporttask.ReportAnalyticsTask
import io.github.janbarari.gradle.extension.ExcludeJacocoGenerated
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.separateElementsWithSpace
import io.github.janbarari.gradle.os.OperatingSystemImp
import io.github.janbarari.gradle.utils.GitUtils
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
abstract class BuildExecutionService :
    BuildService<BuildExecutionService.Params>, OperationCompletionListener, AutoCloseable {

    interface Params : BuildServiceParameters {
        val databaseConfig: Property<GradleAnalyticsPluginConfig.DatabaseConfig>
        val envCI: Property<Boolean>
        val requestedTasks: ListProperty<String>
        val trackingTasks: ListProperty<String>
        val trackingBranches: ListProperty<String>
    }

    private val _executedTasks: ConcurrentLinkedQueue<TaskInfo> = ConcurrentLinkedQueue()

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
    @ExcludeJacocoGenerated
    override fun onFinish(event: FinishEvent?) {
        if (event is TaskFinishEvent) {
            _executedTasks.add(
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
    @ExcludeJacocoGenerated
    override fun close() {

        val db = Database(parameters.databaseConfig.get(), parameters.envCI.get())
        val repo: DatabaseRepository = DatabaseRepositoryImp(
            db, GitUtils.currentBranch(), parameters.requestedTasks.get().separateElementsWithSpace()
        )
        val initializationMetricMedianUseCase = InitializationMetricMedianUseCase(repo)
        val saveMetricUseCase = SaveMetricUseCase(repo, initializationMetricMedianUseCase)
        val saveTemporaryMetricUseCase = SaveTemporaryMetricUseCase(repo)
        val logic: BuildExecutionLogic = BuildExecutionLogicImp(
            saveMetricUseCase,
            saveTemporaryMetricUseCase,
            parameters.databaseConfig.get(),
            parameters.envCI.get(),
            parameters.trackingBranches.get(),
            parameters.trackingTasks.get(),
            parameters.requestedTasks.get()
        )

        if (logic.onExecutionFinished(_executedTasks)) {
            println("New Metric Saved Successfully")
        }

        _executedTasks.clear()
    }

}
