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

import io.github.janbarari.gradle.analytics.domain.model.report.Report
import io.github.janbarari.gradle.analytics.domain.usecase.GetMetricsUseCase
import io.github.janbarari.gradle.analytics.metric.buildsuccessratio.report.CreateBuildSuccessRatioReportStage
import io.github.janbarari.gradle.analytics.metric.buildsuccessratio.report.RenderBuildSuccessRatioReportStage
import io.github.janbarari.gradle.analytics.metric.cachehit.report.CreateCacheHitReportStage
import io.github.janbarari.gradle.analytics.metric.cachehit.report.RenderCacheHitReportStage
import io.github.janbarari.gradle.analytics.metric.configuration.report.CreateConfigurationReportStage
import io.github.janbarari.gradle.analytics.metric.configuration.report.RenderConfigurationReportStage
import io.github.janbarari.gradle.analytics.metric.dependencyresolvemetric.report.CreateDependencyResolveReportStage
import io.github.janbarari.gradle.analytics.metric.dependencyresolvemetric.report.RenderDependencyResolveReportStage
import io.github.janbarari.gradle.analytics.metric.execution.report.CreateExecutionReportStage
import io.github.janbarari.gradle.analytics.metric.execution.report.RenderExecutionReportStage
import io.github.janbarari.gradle.analytics.metric.initialization.report.RenderInitializationReportStage
import io.github.janbarari.gradle.analytics.metric.initialization.report.CreateInitializationReportStage
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.report.CreateModulesMethodCountReportStage
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.report.RenderModulesMethodCountStage
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.report.CreateModulesSourceCountReportStage
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.report.RenderModulesSourceCountStage
import io.github.janbarari.gradle.analytics.metric.totalbuild.report.CreateTotalBuildReportStage
import io.github.janbarari.gradle.analytics.metric.totalbuild.report.RenderTotalBuildReportStage
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
    private val isCI: Boolean,
    private val outputPath: String,
    private val projectName: String,
) : ReportAnalyticsLogic {

    @kotlin.jvm.Throws(EmptyMetricsException::class)
    override suspend fun generateReport(branch: String, requestedTasks: String, period: Long): String {
        val data = getMetricsUseCase.execute(period)

        if (data.isEmpty()) throw EmptyMetricsException()

        val report = CreateReportPipeline(CreateInitializationReportStage(data))
            .addStage(CreateConfigurationReportStage(data))
            .addStage(CreateExecutionReportStage(data))
            .addStage(CreateTotalBuildReportStage(data))
            .addStage(CreateModulesSourceCountReportStage(data))
            .addStage(CreateModulesMethodCountReportStage(data))
            .addStage(CreateCacheHitReportStage(data))
            .addStage(CreateBuildSuccessRatioReportStage(data))
            .addStage(CreateDependencyResolveReportStage(data))
            .execute(Report(branch = branch, requestedTasks = requestedTasks))

        val rawHTML: String = getTextResourceContent("index-template.html")

        val renderInitialReportStage = RenderInitialReportStage.Builder()
            .data(data)
            .projectName(projectName)
            .branch(branch)
            .period(period)
            .requestedTasks(requestedTasks)
            .isCI(isCI)
            .build()
        val renderInitializationReportStage = RenderInitializationReportStage(report)
        val renderConfigurationReportStage = RenderConfigurationReportStage(report)
        val renderExecutionReportStage = RenderExecutionReportStage(report)
        val renderTotalBuildReportStage = RenderTotalBuildReportStage(report)
        val renderModulesSourceCountReportStage = RenderModulesSourceCountStage(report)
        val renderModulesMethodCountReportStage = RenderModulesMethodCountStage(report)
        val renderCacheHitReportStage = RenderCacheHitReportStage(report)
        val renderBuildSuccessRatioReportStage = RenderBuildSuccessRatioReportStage(report)
        val renderDependencyResolveReportStage = RenderDependencyResolveReportStage(report)

        return RenderReportPipeline(renderInitialReportStage)
            .addStage(renderInitializationReportStage)
            .addStage(renderConfigurationReportStage)
            .addStage(renderExecutionReportStage)
            .addStage(renderTotalBuildReportStage)
            .addStage(renderModulesSourceCountReportStage)
            .addStage(renderModulesMethodCountReportStage)
            .addStage(renderCacheHitReportStage)
            .addStage(renderBuildSuccessRatioReportStage)
            .addStage(renderDependencyResolveReportStage)
            .execute(rawHTML)
    }

    @kotlin.jvm.Throws(IOException::class)
    override suspend fun saveReport(renderedHTML: String): Boolean {
        val fontPath = "res/nunito.ttf"
        val logoPath = "res/plugin-logo.png"
        val stylesPath = "res/styles.css"
        val functionsPath = "res/functions.js"
        val chartPath = "res/chart.js"
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

        File("$savePath/$indexPath")
            .writeText(renderedHTML)

        return true
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
