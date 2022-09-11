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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.reporttask

import io.github.janbarari.gradle.analytics.domain.model.Module
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.analytics.domain.usecase.GetMetricsUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.GetModulesTimelineUseCase
import io.github.janbarari.gradle.analytics.metric.buildstatus.render.CreateBuildStatusReportStage
import io.github.janbarari.gradle.analytics.metric.buildstatus.render.RenderBuildStatusReportStage
import io.github.janbarari.gradle.analytics.metric.cachehit.report.CreateCacheHitReportStage
import io.github.janbarari.gradle.analytics.metric.cachehit.report.RenderCacheHitReportStage
import io.github.janbarari.gradle.analytics.metric.configurationprocess.report.CreateConfigurationProcessReportStage
import io.github.janbarari.gradle.analytics.metric.configurationprocess.report.RenderConfigurationProcessReportStage
import io.github.janbarari.gradle.analytics.metric.dependencydetails.render.CreateDependencyDetailsReportStage
import io.github.janbarari.gradle.analytics.metric.dependencydetails.render.RenderDependencyDetailsReportStage
import io.github.janbarari.gradle.analytics.metric.dependencyresolveprocess.report.CreateDependencyResolveProcessReportStage
import io.github.janbarari.gradle.analytics.metric.dependencyresolveprocess.report.RenderDependencyResolveProcessReportStage
import io.github.janbarari.gradle.analytics.metric.executionprocess.report.CreateExecutionProcessReportStage
import io.github.janbarari.gradle.analytics.metric.executionprocess.report.RenderExecutionProcessReportStage
import io.github.janbarari.gradle.analytics.metric.initializationprocess.report.CreateInitializationProcessReportStage
import io.github.janbarari.gradle.analytics.metric.initializationprocess.report.RenderInitializationProcessReportStage
import io.github.janbarari.gradle.analytics.metric.modulesbuildheatmap.render.CreateModulesBuildHeatmapReportStage
import io.github.janbarari.gradle.analytics.metric.modulesbuildheatmap.render.RenderModulesBuildHeatmapReportStage
import io.github.janbarari.gradle.analytics.metric.modulescrashcount.render.CreateModulesCrashCountReportStage
import io.github.janbarari.gradle.analytics.metric.modulescrashcount.render.RenderModulesCrashCountReportStage
import io.github.janbarari.gradle.analytics.metric.modulesdependencygraph.report.CreateModulesDependencyGraphReportStage
import io.github.janbarari.gradle.analytics.metric.modulesdependencygraph.report.RenderModulesDependencyGraphReportStage
import io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.report.CreateModulesExecutionProcessReportStage
import io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.report.RenderModulesExecutionProcessReportStage
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.report.CreateModulesMethodCountReportStage
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.report.RenderModulesMethodCountStage
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.report.CreateModulesSourceCountReportStage
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.report.RenderModulesSourceCountStage
import io.github.janbarari.gradle.analytics.metric.modulessourcesize.report.CreateModulesSourceSizeReportStage
import io.github.janbarari.gradle.analytics.metric.modulessourcesize.report.RenderModulesSourceSizeReportStage
import io.github.janbarari.gradle.analytics.metric.modulestimeline.render.CreateModulesTimelineReportStage
import io.github.janbarari.gradle.analytics.metric.modulestimeline.render.RenderModulesTimelineReportStage
import io.github.janbarari.gradle.analytics.metric.noncacheabletasks.render.CreateNonCacheableTasksReportStage
import io.github.janbarari.gradle.analytics.metric.noncacheabletasks.render.RenderNonCacheableTasksReportStage
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.report.CreateOverallBuildProcessReportStage
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.report.RenderOverallBuildProcessReportStage
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.report.CreateParallelExecutionRateReportStage
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.report.RenderParallelExecutionRateReportStage
import io.github.janbarari.gradle.analytics.metric.successbuildrate.report.CreateSuccessBuildRateReportStage
import io.github.janbarari.gradle.analytics.metric.successbuildrate.report.RenderSuccessBuildRateReportStage
import io.github.janbarari.gradle.analytics.reporttask.exception.EmptyMetricsException
import io.github.janbarari.gradle.analytics.reporttask.exception.InvalidPropertyException
import io.github.janbarari.gradle.analytics.reporttask.exception.MissingPropertyException
import io.github.janbarari.gradle.extension.getSafeResourceAsStream
import io.github.janbarari.gradle.extension.getTextResourceContent
import io.github.janbarari.gradle.extension.hasSpace
import io.github.janbarari.gradle.extension.toRealPath
import io.github.janbarari.gradle.utils.DateTimeUtils
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

/**
 * In order to make the [io.github.janbarari.gradle.analytics.reporttask.ReportAnalyticsTask]
 * testable and the logic framework independent.
 */
class ReportAnalyticsLogicImp(
    private val getMetricsUseCase: GetMetricsUseCase,
    private val getModulesTimelineUseCase: GetModulesTimelineUseCase,
    private val isCI: Boolean,
    private val outputPath: String,
    private val projectName: String,
    private val modules: List<Module>
) : ReportAnalyticsLogic {

    @kotlin.jvm.Throws(EmptyMetricsException::class)
    override suspend fun generateReport(branch: String, requestedTasks: String, period: String): String {
        val convertedPeriod = convertQueryToPeriod(period)
        val data = getMetricsUseCase.execute(convertedPeriod)

        if (data.isEmpty()) throw EmptyMetricsException()

        val report = generateReport(
            data = data, branch = branch, requestedTasks = requestedTasks
        )

        return generateRender(
            data = data, report = report, branch = branch, requestedTasks = requestedTasks, period = convertedPeriod
        )
    }

    private suspend fun generateReport(data: List<BuildMetric>, branch: String, requestedTasks: String): Report {
        return CreateReportPipeline(CreateInitializationProcessReportStage(data)).addStage(
                CreateConfigurationProcessReportStage(
                    data
                )
            ).addStage(CreateExecutionProcessReportStage(data)).addStage(CreateOverallBuildProcessReportStage(data))
            .addStage(CreateModulesSourceCountReportStage(data)).addStage(CreateModulesMethodCountReportStage(data))
            .addStage(CreateCacheHitReportStage(data)).addStage(CreateSuccessBuildRateReportStage(data))
            .addStage(CreateDependencyResolveProcessReportStage(data)).addStage(CreateParallelExecutionRateReportStage(data))
            .addStage(CreateModulesExecutionProcessReportStage(modules, data))
            .addStage(CreateModulesDependencyGraphReportStage(data))
            .addStage(CreateModulesTimelineReportStage(branch, getModulesTimelineUseCase))
            .addStage(CreateBuildStatusReportStage(modules, data)).addStage(CreateModulesBuildHeatmapReportStage(data))
            .addStage(CreateDependencyDetailsReportStage(data)).addStage(CreateNonCacheableTasksReportStage(data))
            .addStage(CreateModulesSourceSizeReportStage(data)).addStage(CreateModulesCrashCountReportStage(modules, data))
            .execute(
                Report(
                    branch = branch, requestedTasks = requestedTasks
                )
            )
    }

    private suspend fun generateRender(
        data: List<BuildMetric>, report: Report, branch: String, requestedTasks: String, period: Pair<Long, Long>
    ): String {
        val rawHTML: String = getTextResourceContent("index-template.html")

        val renderInitialReportStage = RenderInitialReportStage.Builder().data(data).projectName(projectName).branch(branch)
            .gitHeadCommitHash(data.last().gitHeadCommitHash.replace("\"", "")).period(period).requestedTasks(requestedTasks)
            .isCI(isCI).build()

        return RenderReportPipeline(renderInitialReportStage).addStage(RenderInitializationProcessReportStage(report))
            .addStage(RenderConfigurationProcessReportStage(report)).addStage(RenderExecutionProcessReportStage(report))
            .addStage(RenderOverallBuildProcessReportStage(report)).addStage(RenderModulesSourceCountStage(report))
            .addStage(RenderModulesMethodCountStage(report)).addStage(RenderCacheHitReportStage(report))
            .addStage(RenderSuccessBuildRateReportStage(report)).addStage(RenderDependencyResolveProcessReportStage(report))
            .addStage(RenderParallelExecutionRateReportStage(report)).addStage(RenderModulesExecutionProcessReportStage(report))
            .addStage(RenderModulesDependencyGraphReportStage(report)).addStage(RenderModulesTimelineReportStage(report))
            .addStage(RenderBuildStatusReportStage(report)).addStage(RenderModulesBuildHeatmapReportStage(report))
            .addStage(RenderDependencyDetailsReportStage(report)).addStage(RenderNonCacheableTasksReportStage(report))
            .addStage(RenderModulesSourceSizeReportStage(report)).addStage(RenderModulesCrashCountReportStage(report))
            .execute(rawHTML)
    }

    @kotlin.jvm.Throws(IOException::class)
    override suspend fun saveReport(renderedHTML: String): String {
        val resources = listOf(
            "nunito.ttf", "plugin-logo.png", "styles.css", "functions.js", "chart.js", "mermaid.js", "d3.js", "timeline.js"
        )
        val savePath = "${outputPath.toRealPath()}/gradle-analytics-plugin"

        //copy resources
        resources.forEach { resource ->
            FileUtils.copyInputStreamToFile(
                javaClass.getSafeResourceAsStream("/res/$resource"), File("$savePath/res/$resource")
            )
        }

        //write index.html
        File("$savePath/index.html").writeText(renderedHTML)

        return "$savePath/index.html"
    }

    /**
     * Ensures the `--branch` input argument is set and valid.
     */
    @kotlin.jvm.Throws(MissingPropertyException::class, InvalidPropertyException::class)
    override fun ensureBranchArgumentValid(branchArgument: String) {
        if (branchArgument.isEmpty()) throw MissingPropertyException("`--branch` is not present!")
        if (branchArgument.hasSpace()) throw InvalidPropertyException("`--branch` is not valid!")
    }

    /**
     * Ensures the `--period` input argument is set and valid.
     */
    @kotlin.jvm.Throws(MissingPropertyException::class, InvalidPropertyException::class)
    override fun ensurePeriodArgumentValid(periodArgument: String) {
        if (periodArgument.isEmpty()) throw MissingPropertyException("`--period` is not present!")
        convertQueryToPeriod(periodArgument)
    }

    /**
     * Ensures the `--task` input argument is set and valid.
     */
    @kotlin.jvm.Throws(MissingPropertyException::class)
    override fun ensureTaskArgumentValid(requestedTasksArgument: String) {
        if (requestedTasksArgument.isEmpty()) throw MissingPropertyException("`--task` is not present!")
    }

    @kotlin.jvm.Throws(InvalidPropertyException::class)
    override fun convertQueryToPeriod(query: String): Pair<Long, Long> {

        @Suppress("ComplexCondition")
        fun isCorrectlySorted(query: String): Boolean {
            val temp = query.split(" ").filter { it.isNotEmpty() }.map { it.last() }

            return when (temp) {
                listOf('y', 'm', 'd') -> true
                listOf('y', 'd') -> true
                listOf('y', 'm') -> true
                listOf('y') -> true
                listOf('m', 'd') -> true
                listOf('m') -> true
                listOf('d') -> true
                else -> false
            }
        }

        try {
            // today
            if (query == "today") return DateTimeUtils.getDayStartMs() to DateTimeUtils.getDayEndMs()

            // custom dates
            if (query.contains(",")) {
                var startDate = query.split(",")[0]
                var endDate = query.split(",")[1]
                startDate = startDate.replace("s:", "")
                endDate = endDate.replace("e:", "")
                val start = DateTimeUtils.convertDateToEpochMilli(startDate)
                val end = DateTimeUtils.convertDateToEpochMilli(endDate)
                return start to end
            }

            // relative
            var years = 0
            var months = 0
            var days = 0
            if (query.chars().filter { it == 'y'.code }.count() >= 2 || query.chars().filter { it == 'm'.code }
                    .count() >= 2 || query.chars().filter { it == 'd'.code }
                    .count() >= 2) throw InvalidPropertyException("--period has duplicate input")

            query.split(" ").forEach {
                    if (it.last() == 'y') years = it.substring(0, it.length - 1).toInt()
                    else if (it.last() == 'm') months = it.substring(0, it.length - 1).toInt()
                    else if (it.last() == 'd') days = it.substring(0, it.length - 1).toInt()
                    else throw InvalidPropertyException("--period is wrong")

                }

            if (!isCorrectlySorted(query)) throw InvalidPropertyException("--period is wrong sorted")

            val duration: Long = (years * 32_140_800_000L) + (months * 2_678_400_000L) + (days * 86_400_000L)
            if (duration > 32_140_800_000L) throw InvalidPropertyException("--period can not be more than 1 year!")

            val start = DateTimeUtils.getDayStartMs() - duration
            val end = DateTimeUtils.getDayEndMs()
            return start to end
        } catch (e: Throwable) {
            if (e is InvalidPropertyException) throw e
            throw InvalidPropertyException(
                "--period can be like \"today\", \"s:yyyy/MM/dd,e:yyyy/MM/dd\", \"1y\", \"4m\", \"38d\", \"3m 06d\""
            )
        }
    }

}
