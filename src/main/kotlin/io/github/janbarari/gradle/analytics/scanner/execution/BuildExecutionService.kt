/**
 * MIT License
 * Copyright (c) 2024 Mehdi Janbarari (@janbarari)
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
import io.github.janbarari.gradle.analytics.DatabaseConfig
import io.github.janbarari.gradle.analytics.GradleAnalyticsPlugin
import io.github.janbarari.gradle.analytics.database.MySqlDatabaseConnection
import io.github.janbarari.gradle.analytics.database.SqliteDatabaseConnection
import io.github.janbarari.gradle.analytics.domain.model.Module
import io.github.janbarari.gradle.analytics.domain.model.ModulesDependencyGraph
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.analytics.reporttask.ReportAnalyticsTask
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.separateElementsWithSpace
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.utils.ConsolePrinter
import org.gradle.api.JavaVersion
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.Failure
import org.gradle.tooling.events.FailureResult
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.SkippedResult
import org.gradle.tooling.events.SuccessResult
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.OperationDescriptor
import org.gradle.tooling.events.task.TaskFailureResult
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskSuccessResult
import org.gradle.util.GradleVersion
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Tracks the task's execution information.
 */
@ExcludeJacocoGenerated
abstract class BuildExecutionService : BuildService<BuildExecutionService.Params>, OperationCompletionListener, AutoCloseable {

    companion object {
        private val clazz = BuildExecutionService::class.java
    }

    /**
     * **Important Note**
     *
     * Using "is", "get", "set" prefix is not allowed to be used as parameter name.
     */
    interface Params : BuildServiceParameters {
        val enabled: Property<Boolean>
        val databaseConfig: Property<DatabaseConfig>
        val envCI: Property<Boolean>
        val requestedTasks: ListProperty<String>
        val trackingTasks: SetProperty<String>
        val trackingBranches: SetProperty<String>
        val trackAllBranchesEnabled: Property<Boolean>
        val modules: SetProperty<Module>
        val modulesDependencyGraph: Property<ModulesDependencyGraph>
        val nonCacheableTasks: SetProperty<String>
        val outputPath: Property<String>
        val maximumWorkerCount: Property<Int>
        val gitCurrentBranch: Property<String>
        val gitHeadCommitHash: Property<String>
    }

    private val injector = BuildExecutionInjector(parameters)
    private val tower: Tower = injector.provideTower()

    private val executedTasks: ConcurrentLinkedQueue<TaskInfo> = ConcurrentLinkedQueue()

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
        if (isPluginDisabled()) return

        if (isForbiddenTasksRequested()) return

        if (!isTaskTrackable()) return

        if (!isBranchTrackable()) return

        tower.r("build process started")
        tower.r("plugin version: ${GradleAnalyticsPlugin.PLUGIN_VERSION}")
        tower.r("jvm: ${JavaVersion.current()}")
        tower.r("gradle version: ${GradleVersion.current().version}")
        tower.r("requested tasks: ${parameters.requestedTasks.get().separateElementsWithSpace()}")
        tower.r("modules count: ${parameters.modules.get().size}")
        tower.r("ci: ${parameters.envCI.get()}")
        tower.r("tracking tasks count: ${parameters.trackingTasks.get().size}")
        tower.r("tracking branches count: ${parameters.trackingBranches.get().size}")
        tower.r("maximum worker count: " + "${parameters.maximumWorkerCount.get()}")

        printConfigurationNotices()

        if (!isDatabaseConfigValid()) {
            tower.e(clazz, "close()", "database configuration is not valid, process stopped")
            return
        }

        injector.provideBuildExecutionLogic().onExecutionFinished(executedTasks)
        injector.destroy()
        executedTasks.clear()

        tower.r("build process finished")
        tower.save()
    }

    private fun isForbiddenTasksRequested(): Boolean {
        return parameters.requestedTasks.get().contains(ReportAnalyticsTask.TASK_NAME)
    }

    private fun isPluginDisabled(): Boolean {
        return parameters.enabled.get() == false
    }

    private fun isTaskTrackable(): Boolean {
        val requestedTasks = parameters.requestedTasks.get().separateElementsWithSpace()
        return parameters.trackingTasks.get().contains(requestedTasks)
    }

    private fun isBranchTrackable(): Boolean {
        if (parameters.trackAllBranchesEnabled.get()) return true
        return parameters.trackingBranches.get().contains(parameters.gitCurrentBranch.get())
    }

    private fun isDatabaseConfigValid(): Boolean {
        // return false if local machine executed and the config is not set.
        if (parameters.databaseConfig.get().local.isNull() && !parameters.envCI.get()) {
            return false
        }
        if (parameters.databaseConfig.get().local.isNotNull()) {
            when (parameters.databaseConfig.get().local) {
                is SqliteDatabaseConnection -> {
                    val sqliteConfig = parameters.databaseConfig.get().local!! as SqliteDatabaseConnection
                    if (sqliteConfig.path.isNull()) {
                        return false
                    }
                    if (sqliteConfig.name.isNull()) {
                        return false
                    }
                }
                is MySqlDatabaseConnection -> {
                    val mySqlConfig = parameters.databaseConfig.get().local!! as MySqlDatabaseConnection
                    if (mySqlConfig.host.isNull()) {
                        return false
                    }
                    if (mySqlConfig.name.isNull()) {
                        return false
                    }
                }
            }
        }

        //return false if CI machine executed and the config is not set.
        if (parameters.databaseConfig.get().ci.isNull() && parameters.envCI.get()) {
            return false
        }
        if (parameters.databaseConfig.get().ci.isNotNull()) {
            when (parameters.databaseConfig.get().ci) {
                is SqliteDatabaseConnection -> {
                    val sqliteConfig = parameters.databaseConfig.get().ci!! as SqliteDatabaseConnection
                    if (sqliteConfig.path.isNull()) {
                        return false
                    }
                    if (sqliteConfig.name.isNull()) {
                        return false
                    }
                }
                is MySqlDatabaseConnection -> {
                    val mySqlConfig = parameters.databaseConfig.get().ci!! as MySqlDatabaseConnection
                    if (mySqlConfig.host.isNull()) {
                        return false
                    }
                    if (mySqlConfig.name.isNull()) {
                        return false
                    }
                }
            }
        }

        return true
    }

    private fun printConfigurationNotices() {
        tower.i(clazz, "printConfigurationNotices()")
        if (parameters.databaseConfig.get().local.isNull() && parameters.databaseConfig.get().ci.isNull()) {
            tower.w(clazz, "printConfigurationNotices()", "database configuration is missing")

            ConsolePrinter(36).apply {
                printFirstLine()
                printLine("Gradle Analytics Plugin")
                printBreakLine('-')
                printLine("Notice:")
                printLine("Database configuration is missing.")
                printLastLine()
            }
        } else if (parameters.databaseConfig.get().ci.isNull() && parameters.envCI.get()) {
            tower.w(
                clazz = clazz,
                method ="printConfigurationNotices()",
                message = "build ran on ci machine, plugin database config for ci machine is not set"
            )

            ConsolePrinter(74).apply {
                printFirstLine()
                printLine("Gradle Analytics Plugin")
                printBreakLine('-')
                printLine("Notice:")
                printLine("Build ran on ci machine, Plugin database config for ci machine is not set.")
                printLine("It means the plugin won't save the build report in the database.")
                printLastLine()
            }
        } else if (parameters.databaseConfig.get().local.isNull() && !parameters.envCI.get()) {
            tower.w(
                clazz = clazz,
                method ="printConfigurationNotices()",
                message = "build ran on local machine, plugin database config for Local machine is not set"
            )

            ConsolePrinter(80).apply {
                printFirstLine()
                printLine("Gradle Analytics Plugin")
                printBreakLine('-')
                printLine("Notice:")
                printLine("Build ran on Local machine, Plugin database config for Local machine is not set.")
                printLine("It means the plugin won't save the build report in the database.")
                printLastLine()
            }
        }
    }
}
