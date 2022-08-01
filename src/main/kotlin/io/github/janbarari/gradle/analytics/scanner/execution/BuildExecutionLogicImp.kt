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
import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig.DatabaseConfig
import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.os.HardwareInfo
import io.github.janbarari.gradle.analytics.domain.model.os.OsInfo
import io.github.janbarari.gradle.analytics.domain.usecase.SaveMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.SaveTemporaryMetricUseCase
import io.github.janbarari.gradle.analytics.metric.buildsuccessratio.create.CreateBuildSuccessRatioMetricStage
import io.github.janbarari.gradle.analytics.metric.buildsuccessratio.create.CreateBuildSuccessRatioMetricUseCase
import io.github.janbarari.gradle.analytics.metric.cachehit.create.CreateCacheHitMetricStage
import io.github.janbarari.gradle.analytics.metric.cachehit.create.CreateCacheHitMetricUseCase
import io.github.janbarari.gradle.analytics.metric.configuration.create.CreateConfigurationMetricStage
import io.github.janbarari.gradle.analytics.metric.configuration.create.CreateConfigurationMetricUseCase
import io.github.janbarari.gradle.analytics.metric.dependencyresolvemetric.create.CreateDependencyResolveMetricStage
import io.github.janbarari.gradle.analytics.metric.dependencyresolvemetric.create.CreateDependencyResolveMetricUseCase
import io.github.janbarari.gradle.analytics.metric.execution.create.CreateExecutionMetricStage
import io.github.janbarari.gradle.analytics.metric.execution.create.CreateExecutionMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.create.CreateInitializationMetricStage
import io.github.janbarari.gradle.analytics.metric.initialization.create.CreateInitializationMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.create.CreateModulesMethodCountMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.create.CreateModulesMethodCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.create.CreateModulesSourceCountMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.create.CreateModulesSourceCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.parallelratio.create.CreateParallelRatioMetricStage
import io.github.janbarari.gradle.analytics.metric.parallelratio.create.CreateParallelRatioMetricUseCase
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.create.CreateOverallBuildProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.create.CreateOverallBuildProcessMetricUseCase
import io.github.janbarari.gradle.analytics.reporttask.ReportAnalyticsTask
import io.github.janbarari.gradle.analytics.scanner.configuration.BuildConfigurationService
import io.github.janbarari.gradle.analytics.scanner.dependencyresolution.BuildDependencyResolutionService
import io.github.janbarari.gradle.analytics.scanner.initialization.BuildInitializationService
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.separateElementsWithSpace
import io.github.janbarari.gradle.os.provideHardwareInfo
import io.github.janbarari.gradle.os.provideOperatingSystem
import io.github.janbarari.gradle.utils.ConsolePrinter
import io.github.janbarari.gradle.utils.DateTimeUtils
import io.github.janbarari.gradle.utils.GitUtils
import kotlinx.coroutines.runBlocking

/**
 * Implementation of [io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionLogic].
 */
class BuildExecutionLogicImp(
    private val saveMetricUseCase: SaveMetricUseCase,
    private val saveTemporaryMetricUseCase: SaveTemporaryMetricUseCase,
    private val createInitializationMetricUseCase: CreateInitializationMetricUseCase,
    private val createConfigurationMetricUseCase: CreateConfigurationMetricUseCase,
    private val createExecutionMetricUseCase: CreateExecutionMetricUseCase,
    private val createOverallBuildProcessMetricUseCase: CreateOverallBuildProcessMetricUseCase,
    private val createModulesSourceCountMetricUseCase: CreateModulesSourceCountMetricUseCase,
    private val createModulesMethodCountMetricUseCase: CreateModulesMethodCountMetricUseCase,
    private val createCacheHitMetricUseCase: CreateCacheHitMetricUseCase,
    private val createBuildSuccessRatioMetricUseCase: CreateBuildSuccessRatioMetricUseCase,
    private val createDependencyResolveMetricUseCase: CreateDependencyResolveMetricUseCase,
    private val createParallelRatioMetricUseCase: CreateParallelRatioMetricUseCase,
    private val databaseConfig: DatabaseConfig,
    private val envCI: Boolean,
    private val trackingBranches: List<String>,
    private val trackingTasks: List<String>,
    private val requestedTasks: List<String>,
    private val modulesInfo: List<ModulePath>
) : BuildExecutionLogic {

    override fun onExecutionFinished(executedTasks: Collection<TaskInfo>) = runBlocking {

        if (isForbiddenTasksRequested()) return@runBlocking

        if (!isDatabaseConfigurationValid()) return@runBlocking

        if (!isTaskTrackable()) return@runBlocking

        if (!isBranchTrackable()) return@runBlocking

        val isSuccessful = executedTasks.all { it.isSuccessful }
        val failure = executedTasks.find { !it.isSuccessful && it.failures.isNotNull() }?.failures

        val info = BuildInfo(
            createdAt = System.currentTimeMillis(),
            startedAt = BuildInitializationService.STARTED_AT,
            initializedAt = BuildInitializationService.INITIALIZED_AT,
            configuredAt = BuildConfigurationService.CONFIGURED_AT,
            finishedAt = System.currentTimeMillis(),
            osInfo = OsInfo(provideOperatingSystem().getName()),
            hardwareInfo = HardwareInfo(provideHardwareInfo().availableMemory(), provideHardwareInfo().totalMemory()),
            dependenciesResolveInfo = BuildDependencyResolutionService.dependenciesResolveInfo.values,
            executedTasks = executedTasks.toList(),
            branch = GitUtils.currentBranch(),
            requestedTasks = requestedTasks,
            isSuccessful = isSuccessful,
            failure = failure
        )

        resetDependentServices()

        val createInitializationMetricStage = CreateInitializationMetricStage(info, createInitializationMetricUseCase)
        val createConfigurationMetricStage = CreateConfigurationMetricStage(info, createConfigurationMetricUseCase)
        val createExecutionMetricStage = CreateExecutionMetricStage(info, createExecutionMetricUseCase)
        val createOverallBuildProcessMetricStage =
            CreateOverallBuildProcessMetricStage(info, createOverallBuildProcessMetricUseCase)
        val createModulesSourceCountMetricStage =
            CreateModulesSourceCountMetricStage(modulesInfo, createModulesSourceCountMetricUseCase)
        val createModulesMethodCountMetricStage =
            CreateModulesMethodCountMetricStage(modulesInfo, createModulesMethodCountMetricUseCase)
        val createCacheHitMetricStage = CreateCacheHitMetricStage(info, modulesInfo, createCacheHitMetricUseCase)
        val createBuildSuccessRatioMetricStage = CreateBuildSuccessRatioMetricStage(info, createBuildSuccessRatioMetricUseCase)
        val createDependencyResolveMetricStage = CreateDependencyResolveMetricStage(info, createDependencyResolveMetricUseCase)
        val createParallelRatioMetricStage = CreateParallelRatioMetricStage(info, createParallelRatioMetricUseCase)

        val buildMetric = CreateMetricPipeline(createInitializationMetricStage)
            .addStage(createConfigurationMetricStage)
            .addStage(createExecutionMetricStage)
            .addStage(createOverallBuildProcessMetricStage)
            .addStage(createModulesSourceCountMetricStage)
            .addStage(createModulesMethodCountMetricStage)
            .addStage(createCacheHitMetricStage)
            .addStage(createBuildSuccessRatioMetricStage)
            .addStage(createDependencyResolveMetricStage)
            .addStage(createParallelRatioMetricStage)
            .execute(BuildMetric(info.branch, info.requestedTasks, info.createdAt))

        saveTemporaryMetricUseCase.execute(buildMetric)
        saveMetricUseCase.execute(buildMetric)

        printBuildInfo(buildMetric)
    }

    private fun printBuildInfo(buildMetric: BuildMetric) {
        val requestedTasks = buildMetric.requestedTasks.separateElementsWithSpace()
        val repoLink = "https://github.com/janbarari/gradle-analytics-plugin"

        var width = requestedTasks.length + 25
        if (width < repoLink.length) width = repoLink.length

        ConsolePrinter(width).run {
            printFirstLine()
            printLine("Gradle Analytics Plugin", "")
            printBreakLine('-')
            printLine("Requested Tasks:", requestedTasks)
            printLine("Branch:", buildMetric.branch)
            printBreakLine('-')
            printLine("Build Info", "")
            printLine("Initialization Process:", "${buildMetric.initializationMetric?.average} ms")
            printLine("Configuration Process:", "${buildMetric.configurationMetric?.average} ms")
            printLine("Dependency Resolve Process:", "${buildMetric.dependencyResolveMetric?.average} ms")
            printLine("Execution Process:", "${buildMetric.executionMetric?.average} ms")
            printLine("Overall Build Process:", "${buildMetric.totalBuildMetric?.average} ms")
            printLine("Cache Hit:", "${buildMetric.cacheHitMetric?.hitRatio}%")
            printLine("Parallel Ratio:", "${buildMetric.parallelRatioMetric?.ratio}%")
            printBreakLine('-')
            printLine("Datetime:", DateTimeUtils.formatToDateTime(buildMetric.createdAt))
            printBreakLine('-')
            printLine("Made with ❤ for developers", "")
            printLine(repoLink, "")
            printLine("","↖ Tap the ☆ button to support this plugin")
            printLastLine()
        }

    }

    @ExcludeJacocoGenerated
    override fun resetDependentServices() {
        BuildInitializationService.reset()
        BuildConfigurationService.reset()
        BuildDependencyResolutionService.reset()
    }

    override fun isDatabaseConfigurationValid(): Boolean {
        //return false if local machine executed and the config is not set.
        if (databaseConfig.local.isNull() && !envCI) {
            return false
        }

        //return false if CI machine executed and the config is not set.
        if (databaseConfig.ci.isNull() && envCI) {
            return false
        }

        return true
    }

    override fun isForbiddenTasksRequested(): Boolean {
        return requestedTasks.contains(ReportAnalyticsTask.TASK_NAME)
    }

    override fun isTaskTrackable(): Boolean {
        val requestedTasks = requestedTasks.separateElementsWithSpace()
        return trackingTasks.contains(requestedTasks)
    }

    override fun isBranchTrackable(): Boolean {
        return trackingBranches.contains(GitUtils.currentBranch())
    }

}
