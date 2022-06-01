package io.github.janbarari.gradle.analytics.scanner

import io.github.janbarari.gradle.analytics.domain.model.TaskInfo

interface BuildExecutionLogic {

    fun isBranchTrackable(): Boolean

    fun isTaskTrackable(): Boolean

    fun isForbiddenTasksRequested(): Boolean

    fun isDatabaseConfigurationValid(): Boolean

    fun resetDependentServices()

    fun onExecutionFinished(executedTasks: Collection<TaskInfo>): Boolean

}
