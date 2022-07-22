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
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.HardwareInfo
import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.model.OsInfo
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.analytics.domain.usecase.SaveMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.SaveTemporaryMetricUseCase
import io.github.janbarari.gradle.analytics.metric.cachehit.create.CreateCacheHitMetricStage
import io.github.janbarari.gradle.analytics.metric.cachehit.create.CreateCacheHitMetricUseCase
import io.github.janbarari.gradle.analytics.metric.configuration.create.CreateConfigurationMetricStage
import io.github.janbarari.gradle.analytics.metric.configuration.create.CreateConfigurationMetricUseCase
import io.github.janbarari.gradle.analytics.metric.execution.create.CreateExecutionMetricStage
import io.github.janbarari.gradle.analytics.metric.execution.create.CreateExecutionMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.create.CreateInitializationMetricStage
import io.github.janbarari.gradle.analytics.metric.initialization.create.CreateInitializationMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.create.CreateModulesMethodCountMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.create.CreateModulesMethodCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.create.CreateModulesSourceCountMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.create.CreateModulesSourceCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.totalbuild.CreateTotalBuildMetricStage
import io.github.janbarari.gradle.analytics.metric.totalbuild.CreateTotalBuildMetricUseCase
import io.github.janbarari.gradle.analytics.reporttask.ReportAnalyticsTask
import io.github.janbarari.gradle.analytics.scanner.configuration.BuildConfigurationService
import io.github.janbarari.gradle.analytics.scanner.dependencyresolution.BuildDependencyResolutionService
import io.github.janbarari.gradle.analytics.scanner.initialization.BuildInitializationService
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.launchIO
import io.github.janbarari.gradle.extension.separateElementsWithSpace
import io.github.janbarari.gradle.os.provideHardwareInfo
import io.github.janbarari.gradle.os.provideOperatingSystem
import io.github.janbarari.gradle.utils.GitUtils

/**
 * Implementation of [io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionLogic].
 */
@Suppress("LongParameterList")
class BuildExecutionLogicImp(
    private val saveMetricUseCase: SaveMetricUseCase,
    private val saveTemporaryMetricUseCase: SaveTemporaryMetricUseCase,
    private val createInitializationMetricUseCase: CreateInitializationMetricUseCase,
    private val createConfigurationMetricUseCase: CreateConfigurationMetricUseCase,
    private val createExecutionMetricUseCase: CreateExecutionMetricUseCase,
    private val createTotalBuildMetricUseCase: CreateTotalBuildMetricUseCase,
    private val createModulesSourceCountMetricUseCase: CreateModulesSourceCountMetricUseCase,
    private val createModulesMethodCountMetricUseCase: CreateModulesMethodCountMetricUseCase,
    private val createCacheHitMetricUseCase: CreateCacheHitMetricUseCase,
    private val databaseConfig: DatabaseConfig,
    private val envCI: Boolean,
    private val trackingBranches: List<String>,
    private val trackingTasks: List<String>,
    private val requestedTasks: List<String>,
    private val modulesInfo: List<ModulePath>
) : BuildExecutionLogic {

    override fun onExecutionFinished(executedTasks: Collection<TaskInfo>) {

        if (isForbiddenTasksRequested()) return

        if (!isDatabaseConfigurationValid()) return

        if (!isTaskTrackable()) return

        if (!isBranchTrackable()) return

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
            requestedTasks = requestedTasks
        )

        resetDependentServices()

        val createInitializationMetricStage = CreateInitializationMetricStage(info, createInitializationMetricUseCase)
        val createConfigurationMetricStage = CreateConfigurationMetricStage(info, createConfigurationMetricUseCase)
        val createExecutionMetricStage = CreateExecutionMetricStage(info, createExecutionMetricUseCase)
        val createTotalBuildMetricStage = CreateTotalBuildMetricStage(info, createTotalBuildMetricUseCase)
        val createModulesSourceCountMetricStage = CreateModulesSourceCountMetricStage(
            modulesInfo, createModulesSourceCountMetricUseCase
        )
        val createModulesMethodCountMetricStage = CreateModulesMethodCountMetricStage(
            modulesInfo, createModulesMethodCountMetricUseCase
        )
        val createCacheHitMetricStage = CreateCacheHitMetricStage(
            info, modulesInfo, createCacheHitMetricUseCase
        )

        launchIO {
            val metric = CreateMetricPipeline(createInitializationMetricStage)
                .addStage(createConfigurationMetricStage)
                .addStage(createExecutionMetricStage)
                .addStage(createTotalBuildMetricStage)
                .addStage(createModulesSourceCountMetricStage)
                .addStage(createModulesMethodCountMetricStage)
                .addStage(createCacheHitMetricStage)
                .execute(BuildMetric(info.branch, info.requestedTasks, info.createdAt))

            saveTemporaryMetricUseCase.execute(metric)
            saveMetricUseCase.execute(metric)
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
