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
import io.github.janbarari.gradle.analytics.metric.successbuildrate.create.CreateSuccessBuildRateMetricStage
import io.github.janbarari.gradle.analytics.metric.successbuildrate.create.CreateSuccessBuildRateMetricUseCase
import io.github.janbarari.gradle.analytics.metric.cachehit.create.CreateCacheHitMetricStage
import io.github.janbarari.gradle.analytics.metric.cachehit.create.CreateCacheHitMetricUseCase
import io.github.janbarari.gradle.analytics.metric.configurationprocess.create.CreateConfigurationProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.configurationprocess.create.CreateConfigurationProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.dependencyresolveprocess.create.CreateDependencyResolveProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.dependencyresolveprocess.create.CreateDependencyResolveProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.executionprocess.create.CreateExecutionProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.executionprocess.create.CreateExecutionProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initializationprocess.create.CreateInitializationProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.initializationprocess.create.CreateInitializationProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.create.CreateModulesMethodCountMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.create.CreateModulesMethodCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.create.CreateModulesSourceCountMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.create.CreateModulesSourceCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.create.CreateParallelExecutionRateMetricStage
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.create.CreateParallelExecutionRateMetricUseCase
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
    private val createInitializationProcessMetricUseCase: CreateInitializationProcessMetricUseCase,
    private val createConfigurationProcessMetricUseCase: CreateConfigurationProcessMetricUseCase,
    private val createExecutionProcessMetricUseCase: CreateExecutionProcessMetricUseCase,
    private val createOverallBuildProcessMetricUseCase: CreateOverallBuildProcessMetricUseCase,
    private val createModulesSourceCountMetricUseCase: CreateModulesSourceCountMetricUseCase,
    private val createModulesMethodCountMetricUseCase: CreateModulesMethodCountMetricUseCase,
    private val createCacheHitMetricUseCase: CreateCacheHitMetricUseCase,
    private val createSuccessBuildRateMetricUseCase: CreateSuccessBuildRateMetricUseCase,
    private val createDependencyResolveProcessMetricUseCase: CreateDependencyResolveProcessMetricUseCase,
    private val createParallelExecutionRateMetricUseCase: CreateParallelExecutionRateMetricUseCase,
    private val databaseConfig: DatabaseConfig,
    private val envCI: Boolean,
    private val trackingBranches: List<String>,
    private val trackingTasks: List<String>,
    private val requestedTasks: List<String>,
    private val modulesInfo: List<ModulePath>
) : BuildExecutionLogic {

    @Suppress("LongMethod")
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

        val createInitializationProcessMetricStage =
            CreateInitializationProcessMetricStage(info, createInitializationProcessMetricUseCase)
        val createConfigurationProcessMetricStage =
            CreateConfigurationProcessMetricStage(info, createConfigurationProcessMetricUseCase)
        val createExecutionProcessMetricStage = CreateExecutionProcessMetricStage(info, createExecutionProcessMetricUseCase)
        val createOverallBuildProcessMetricStage =
            CreateOverallBuildProcessMetricStage(info, createOverallBuildProcessMetricUseCase)
        val createModulesSourceCountMetricStage =
            CreateModulesSourceCountMetricStage(modulesInfo, createModulesSourceCountMetricUseCase)
        val createModulesMethodCountMetricStage =
            CreateModulesMethodCountMetricStage(modulesInfo, createModulesMethodCountMetricUseCase)
        val createCacheHitMetricStage = CreateCacheHitMetricStage(info, modulesInfo, createCacheHitMetricUseCase)
        val createSuccessBuildRateMetricStage = CreateSuccessBuildRateMetricStage(info, createSuccessBuildRateMetricUseCase)
        val createDependencyResolveProcessMetricStage =
            CreateDependencyResolveProcessMetricStage(info, createDependencyResolveProcessMetricUseCase)
        val createParallelExecutionRateMetricStage =
            CreateParallelExecutionRateMetricStage(info, createParallelExecutionRateMetricUseCase)

        val buildMetric = CreateMetricPipeline(createInitializationProcessMetricStage)
            .addStage(createConfigurationProcessMetricStage)
            .addStage(createExecutionProcessMetricStage)
            .addStage(createOverallBuildProcessMetricStage)
            .addStage(createModulesSourceCountMetricStage)
            .addStage(createModulesMethodCountMetricStage)
            .addStage(createCacheHitMetricStage)
            .addStage(createSuccessBuildRateMetricStage)
            .addStage(createDependencyResolveProcessMetricStage)
            .addStage(createParallelExecutionRateMetricStage)
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
            printLine("Initialization Process:", "${buildMetric.initializationProcessMetric?.median} ms")
            printLine("Configuration Process:", "${buildMetric.configurationProcessMetric?.median} ms")
            printLine("Dependency Resolve Process:", "${buildMetric.dependencyResolveProcessMetric?.median} ms")
            printLine("Execution Process:", "${buildMetric.executionProcessMetric?.median} ms")
            printLine("Overall Build Process:", "${buildMetric.overallBuildProcessMetric?.median} ms")
            printLine("Cache Hit:", "${buildMetric.cacheHitMetric?.rate}%")
            printLine("Parallel Execution Rate:", "${buildMetric.parallelExecutionRateMetric?.rate}%")
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
