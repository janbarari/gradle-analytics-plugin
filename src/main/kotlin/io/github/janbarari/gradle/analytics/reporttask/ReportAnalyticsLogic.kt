package io.github.janbarari.gradle.analytics.reporttask

import io.github.janbarari.gradle.analytics.reporttask.exception.InvalidPropertyException
import io.github.janbarari.gradle.analytics.reporttask.exception.MissingPropertyException
import java.io.IOException

interface ReportAnalyticsLogic {

    @kotlin.jvm.Throws(IOException::class)
    fun saveReport(renderedHTML: String): Boolean

    fun generateReport(branch: String, requestedTasks: String, period: Long): String

    @kotlin.jvm.Throws(MissingPropertyException::class, InvalidPropertyException::class)
    fun ensureBranchArgumentValid(branchArgument: String)

    @kotlin.jvm.Throws(MissingPropertyException::class, InvalidPropertyException::class)
    fun ensurePeriodArgumentValid(periodArgument: String)

    @kotlin.jvm.Throws(MissingPropertyException::class, InvalidPropertyException::class)
    fun ensureTaskArgumentValid(requestedTasksArgument: String)

}
