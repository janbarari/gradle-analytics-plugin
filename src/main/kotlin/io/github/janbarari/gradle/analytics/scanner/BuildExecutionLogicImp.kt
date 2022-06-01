package io.github.janbarari.gradle.analytics.scanner

import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig.DatabaseConfig
import io.github.janbarari.gradle.analytics.domain.model.BuildInfo
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.HardwareInfo
import io.github.janbarari.gradle.analytics.domain.model.OsInfo
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.analytics.domain.usecase.SaveMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.SaveTemporaryMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.InitializationMetricUseCase
import io.github.janbarari.gradle.analytics.reporttask.ReportAnalyticsTask
import io.github.janbarari.gradle.extension.ExcludeJacocoGenerated
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.separateElementsWithSpace
import io.github.janbarari.gradle.os.OperatingSystemImp
import io.github.janbarari.gradle.utils.GitUtils

@Suppress("LongParameterList")
class BuildExecutionLogicImp(
    private val saveMetricUseCase: SaveMetricUseCase,
    private val saveTemporaryMetricUseCase: SaveTemporaryMetricUseCase,
    private val databaseConfig: DatabaseConfig,
    private val envCI: Boolean,
    private val trackingBranches: List<String>,
    private val trackingTasks: List<String>,
    private val requestedTasks: List<String>
) : BuildExecutionLogic {

    @Suppress("ReturnCount")
    override fun onExecutionFinished(executedTasks: Collection<TaskInfo>): Boolean {
        if (isForbiddenTasksRequested() || !isDatabaseConfigurationValid()) return false
        if (!isTaskTrackable() || !isBranchTrackable()) return false

        val info = BuildInfo(
            startedAt = BuildInitializationService.STARTED_AT,
            initializedAt = BuildInitializationService.INITIALIZED_AT,
            configuredAt = BuildConfigurationService.CONFIGURED_AT,
            finishedAt = System.currentTimeMillis(),
            osInfo = OsInfo(OperatingSystemImp().getName()),
            hardwareInfo = HardwareInfo(0, 0),
            dependenciesResolveInfo = BuildDependencyResolutionService.dependenciesResolveInfo.values,
            executedTasks = executedTasks
        )

        resetDependentServices()

        val metric = BuildMetric(
            branch = GitUtils.currentBranch(),
            requestedTasks = requestedTasks,
            createdAt = System.currentTimeMillis()
        ).apply {

            initializationMetric = InitializationMetricUseCase().execute(
                info.getInitializationDuration().toMillis()
            )

        }

        saveTemporaryMetricUseCase.execute(metric)
        saveMetricUseCase.execute(metric)

        return true
    }

    @ExcludeJacocoGenerated
    override fun resetDependentServices() {
        BuildInitializationService.reset()
        BuildConfigurationService.reset()
        BuildDependencyResolutionService.reset()
    }

    @Suppress("ReturnCount")
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

    /**
     * Checks the current git branch is listed in tracking branches.
     * @return true/false
     */
    override fun isBranchTrackable(): Boolean {
        return trackingBranches.contains(GitUtils.currentBranch())
    }

}
