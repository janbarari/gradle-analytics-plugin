package io.github.janbarari.gradle.analytics.core.gradlebuild

import io.github.janbarari.gradle.analytics.core.task.TaskReport

class GradleBuild(
    private var onBuildFinishListener: OnBuildFinishListener
) {

    interface OnBuildFinishListener {
        fun onBuildFinished(buildReport: BuildReport)
    }

    private var startTime: Long = 0L
    private var endTime: Long = 0L

    fun processStarted() {
        startTime = System.currentTimeMillis()
    }

    fun processFinished(taskReports: Collection<TaskReport>) {
        endTime = System.currentTimeMillis()

        val buildReport = BuildReport(
            startTime,
            endTime,
            taskReports
        )
        onBuildFinishListener.onBuildFinished(buildReport)
    }

}
