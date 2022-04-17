package io.github.janbarari.gradle.analytics.core.gradlebuild

import io.github.janbarari.gradle.analytics.core.exception.WrongEndTimeException
import io.github.janbarari.gradle.analytics.core.task.TaskReport
import java.time.Duration

class BuildReport(
    val startTime: Long,
    val endTime: Long,
    val taskReports: Collection<TaskReport>
): java.io.Serializable {

    fun getDuration(): Duration {
        if (endTime < startTime) {
            throw WrongEndTimeException()
        }
        return Duration.ofMillis(endTime - startTime)
    }

}