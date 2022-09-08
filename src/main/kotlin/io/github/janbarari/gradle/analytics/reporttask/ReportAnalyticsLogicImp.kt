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
import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.analytics.domain.usecase.GetMetricsUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.GetModulesTimelineUseCase
import io.github.janbarari.gradle.analytics.metric.buildstatus.render.CreateBuildStatusReportStage
import io.github.janbarari.gradle.analytics.metric.buildstatus.render.RenderBuildStatusReportStage
import io.github.janbarari.gradle.analytics.metric.successbuildrate.report.CreateSuccessBuildRateReportStage
import io.github.janbarari.gradle.analytics.metric.successbuildrate.report.RenderSuccessBuildRateReportStage
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
import io.github.janbarari.gradle.analytics.metric.initializationprocess.report.RenderInitializationProcessReportStage
import io.github.janbarari.gradle.analytics.metric.initializationprocess.report.CreateInitializationProcessReportStage
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
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.report.CreateParallelExecutionRateReportStage
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.report.RenderParallelExecutionRateReportStage
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.report.CreateOverallBuildProcessReportStage
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.report.RenderOverallBuildProcessReportStage
import io.github.janbarari.gradle.analytics.reporttask.exception.EmptyMetricsException
import io.github.janbarari.gradle.analytics.reporttask.exception.InvalidPropertyException
import io.github.janbarari.gradle.analytics.reporttask.exception.MissingPropertyException
import io.github.janbarari.gradle.extension.getSafeResourceAsStream
import io.github.janbarari.gradle.extension.getTextResourceContent
import io.github.janbarari.gradle.extension.hasSpace
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.toRealPath
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
    @Suppress("LongMethod")
    override suspend fun generateReport(branch: String, requestedTasks: String, period: Long): String {
        val data = getMetricsUseCase.execute(period)

        if (data.isEmpty()) throw EmptyMetricsException()

        val report = CreateReportPipeline(CreateInitializationProcessReportStage(data))
            .addStage(CreateConfigurationProcessReportStage(data))
            .addStage(CreateExecutionProcessReportStage(data))
            .addStage(CreateOverallBuildProcessReportStage(data))
            .addStage(CreateModulesSourceCountReportStage(data))
            .addStage(CreateModulesMethodCountReportStage(data))
            .addStage(CreateCacheHitReportStage(data))
            .addStage(CreateSuccessBuildRateReportStage(data))
            .addStage(CreateDependencyResolveProcessReportStage(data))
            .addStage(CreateParallelExecutionRateReportStage(data))
            .addStage(CreateModulesExecutionProcessReportStage(modules, data))
            .addStage(CreateModulesDependencyGraphReportStage(data))
            .addStage(CreateModulesTimelineReportStage(branch, getModulesTimelineUseCase))
            .addStage(CreateBuildStatusReportStage(modules, data))
            .addStage(CreateModulesBuildHeatmapReportStage(data))
            .addStage(CreateDependencyDetailsReportStage(data))
            .addStage(CreateNonCacheableTasksReportStage(data))
            .addStage(CreateModulesSourceSizeReportStage(data))
            .addStage(CreateModulesCrashCountReportStage(modules, data))
            .execute(
                Report(
                    branch = branch,
                    requestedTasks = requestedTasks
                )
            )

        val rawHTML: String = getTextResourceContent("index-template.html")

        val renderInitialReportStage = RenderInitialReportStage.Builder()
            .data(data)
            .projectName(projectName)
            .branch(branch)
            .gitHeadCommitHash(data.last().gitHeadCommitHash.replace("\"", ""))
            .period(period)
            .requestedTasks(requestedTasks)
            .isCI(isCI)
            .build()

        val renderInitializationProcessReportStage = RenderInitializationProcessReportStage(report)
        val renderConfigurationProcessReportStage = RenderConfigurationProcessReportStage(report)
        val renderExecutionProcessReportStage = RenderExecutionProcessReportStage(report)
        val renderOverallBuildProcessReportStage = RenderOverallBuildProcessReportStage(report)
        val renderModulesSourceCountReportStage = RenderModulesSourceCountStage(report)
        val renderModulesMethodCountReportStage = RenderModulesMethodCountStage(report)
        val renderCacheHitReportStage = RenderCacheHitReportStage(report)
        val renderSuccessBuildRateReportStage = RenderSuccessBuildRateReportStage(report)
        val renderDependencyResolveProcessReportStage = RenderDependencyResolveProcessReportStage(report)
        val renderParallelExecutionRateReportStage = RenderParallelExecutionRateReportStage(report)
        val renderModulesExecutionProcessReportStage = RenderModulesExecutionProcessReportStage(report)
        val renderModulesDependencyGraphReportStage = RenderModulesDependencyGraphReportStage(report)
        val renderModulesTimelineReportStage = RenderModulesTimelineReportStage(report)
        val renderBuildStatusReportStage = RenderBuildStatusReportStage(report)
        val renderModulesBuildHeatmapReportStage = RenderModulesBuildHeatmapReportStage(report)
        val renderDependencyDetailsReportStage = RenderDependencyDetailsReportStage(report)
        val renderNonCacheableTasksReportStage = RenderNonCacheableTasksReportStage(report)
        val renderModulesSourceSizeReportStage = RenderModulesSourceSizeReportStage(report)
        val renderModulesCrashCountReportStage = RenderModulesCrashCountReportStage(report)

        return RenderReportPipeline(renderInitialReportStage)
            .addStage(renderInitializationProcessReportStage)
            .addStage(renderConfigurationProcessReportStage)
            .addStage(renderExecutionProcessReportStage)
            .addStage(renderOverallBuildProcessReportStage)
            .addStage(renderModulesSourceCountReportStage)
            .addStage(renderModulesMethodCountReportStage)
            .addStage(renderCacheHitReportStage)
            .addStage(renderSuccessBuildRateReportStage)
            .addStage(renderDependencyResolveProcessReportStage)
            .addStage(renderParallelExecutionRateReportStage)
            .addStage(renderModulesExecutionProcessReportStage)
            .addStage(renderModulesDependencyGraphReportStage)
            .addStage(renderModulesTimelineReportStage)
            .addStage(renderBuildStatusReportStage)
            .addStage(renderModulesBuildHeatmapReportStage)
            .addStage(renderDependencyDetailsReportStage)
            .addStage(renderNonCacheableTasksReportStage)
            .addStage(renderModulesSourceSizeReportStage)
            .addStage(renderModulesCrashCountReportStage)
            .execute(rawHTML)
    }

    @kotlin.jvm.Throws(IOException::class)
    override suspend fun saveReport(renderedHTML: String): String {
        val fontPath = "res/nunito.ttf"
        val logoPath = "res/plugin-logo.png"
        val stylesPath = "res/styles.css"
        val functionsPath = "res/functions.js"
        val chartPath = "res/chart.js"
        val mermaidPath = "res/mermaid.js"
        val d3Path = "res/d3.js"
        val timelinePath = "res/timeline.js"
        val indexPath = "index.html"
        val savePath = "${outputPath.toRealPath()}/gradle-analytics-plugin"

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/$fontPath"),
            File("$savePath/$fontPath")
        )

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/$logoPath"),
            File("$savePath/$logoPath")
        )

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/$functionsPath"),
            File("$savePath/$functionsPath")
        )

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/$chartPath"),
            File("$savePath/$chartPath")
        )

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/$stylesPath"),
            File("$savePath/$stylesPath")
        )

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/$mermaidPath"),
            File("$savePath/$mermaidPath")
        )

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/$d3Path"),
            File("$savePath/$d3Path")
        )

        FileUtils.copyInputStreamToFile(
            javaClass.getSafeResourceAsStream("/$timelinePath"),
            File("$savePath/$timelinePath")
        )

        File("$savePath/$indexPath")
            .writeText(renderedHTML)

        return "$savePath/$indexPath"
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
        if (periodArgument.toLongOrNull().isNull())
            throw InvalidPropertyException("`--period` is not valid!, Period should be a number between 1 to 12.")
    }

    /**
     * Ensures the `--task` input argument is set and valid.
     */
    @kotlin.jvm.Throws(MissingPropertyException::class)
    override fun ensureTaskArgumentValid(requestedTasksArgument: String) {
        if (requestedTasksArgument.isEmpty()) throw MissingPropertyException("`--task` is not present!")
    }

}
