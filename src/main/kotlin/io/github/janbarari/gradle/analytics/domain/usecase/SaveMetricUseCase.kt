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
package io.github.janbarari.gradle.analytics.domain.usecase

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.metric.successbuildrate.update.UpdateSuccessBuildRateMetricStage
import io.github.janbarari.gradle.analytics.metric.successbuildrate.update.UpdateSuccessBuildRateMetricUseCase
import io.github.janbarari.gradle.analytics.metric.cachehit.update.UpdateCacheHitMetricStage
import io.github.janbarari.gradle.analytics.metric.cachehit.update.UpdateCacheHitMetricUseCase
import io.github.janbarari.gradle.analytics.metric.configurationprocess.update.UpdateConfigurationProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.configurationprocess.update.UpdateConfigurationProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.dependencydetails.update.UpdateDependencyDetailsMetricStage
import io.github.janbarari.gradle.analytics.metric.dependencydetails.update.UpdateDependencyDetailsMetricUseCase
import io.github.janbarari.gradle.analytics.metric.dependencyresolveprocess.update.UpdateDependencyResolveProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.dependencyresolveprocess.update.UpdateDependencyResolveProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.executionprocess.update.UpdateExecutionProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.executionprocess.update.UpdateExecutionProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initializationprocess.update.UpdateInitializationProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.initializationprocess.update.UpdateInitializationProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesbuildheatmap.update.UpdateModulesBuildHeatmapMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesbuildheatmap.update.UpdateModulesBuildHeatmapMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesdependencygraph.update.UpdateModulesDependencyGraphMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesdependencygraph.update.UpdateModulesDependencyGraphMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.update.UpdateModulesExecutionProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.update.UpdateModulesExecutionProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.update.UpdateModulesMethodCountMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.update.UpdateModulesMethodCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.update.UpdateModulesSourceCountMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.update.UpdateModulesSourceCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.update.UpdateParallelExecutionRateMetricStage
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.update.UpdateParallelExecutionRateMetricUseCase
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.update.UpdateOverallBuildProcessMetricStage
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.update.UpdateOverallBuildProcessMetricUseCase
import io.github.janbarari.gradle.core.UseCase

/**
 * Saves daily build metrics.
 */
class SaveMetricUseCase(
    private val repo: DatabaseRepository,
    private val updateInitializationProcessMetricUseCase: UpdateInitializationProcessMetricUseCase,
    private val updateConfigurationProcessMetricUseCase: UpdateConfigurationProcessMetricUseCase,
    private val updateExecutionProcessMetricUseCase: UpdateExecutionProcessMetricUseCase,
    private val updateOverallBuildProcessMetricUseCase: UpdateOverallBuildProcessMetricUseCase,
    private val updateModulesSourceCountMetricUseCase: UpdateModulesSourceCountMetricUseCase,
    private val updateModulesMethodCountMetricUseCase: UpdateModulesMethodCountMetricUseCase,
    private val updateCacheHitMetricUseCase: UpdateCacheHitMetricUseCase,
    private val updateSuccessBuildRateMetricUseCase: UpdateSuccessBuildRateMetricUseCase,
    private val updateDependencyResolveProcessMetricUseCase: UpdateDependencyResolveProcessMetricUseCase,
    private val updateParallelExecutionRateMetricUseCase: UpdateParallelExecutionRateMetricUseCase,
    private val updateModulesExecutionProcessMetricUseCase: UpdateModulesExecutionProcessMetricUseCase,
    private val updateModulesDependencyGraphMetricUseCase: UpdateModulesDependencyGraphMetricUseCase,
    private val updateModulesBuildHeatmapMetricUseCase: UpdateModulesBuildHeatmapMetricUseCase,
    private val updateDependencyDetailsMetricUseCase: UpdateDependencyDetailsMetricUseCase
) : UseCase<BuildMetric, Long>() {

    /**
     * Saves/Updates the daily metric in the database.
     */
    override suspend fun execute(input: BuildMetric): Long {

        if (repo.isDayMetricExists()) {

            val updateInitializationProcessMetricStage =
                UpdateInitializationProcessMetricStage(updateInitializationProcessMetricUseCase)
            val updateConfigurationProcessMetricStage =
                UpdateConfigurationProcessMetricStage(updateConfigurationProcessMetricUseCase)
            val updateExecutionProcessMetricStage = UpdateExecutionProcessMetricStage(updateExecutionProcessMetricUseCase)
            val updateOverallBuildProcessMetricStage =
                UpdateOverallBuildProcessMetricStage(updateOverallBuildProcessMetricUseCase)
            val updateModulesSourceCountMetricStage = UpdateModulesSourceCountMetricStage(updateModulesSourceCountMetricUseCase)
            val updateModulesMethodCountMetricStage = UpdateModulesMethodCountMetricStage(updateModulesMethodCountMetricUseCase)
            val updateCacheHitMetricStage = UpdateCacheHitMetricStage(updateCacheHitMetricUseCase)
            val updateSuccessBuildRateMetricStage = UpdateSuccessBuildRateMetricStage(updateSuccessBuildRateMetricUseCase)
            val updateDependencyResolveProcessMetricStage =
                UpdateDependencyResolveProcessMetricStage(updateDependencyResolveProcessMetricUseCase)
            val updateParallelExecutionRateMetricStage =
                UpdateParallelExecutionRateMetricStage(updateParallelExecutionRateMetricUseCase)
            val updateModulesExecutionProcessMetricStage = UpdateModulesExecutionProcessMetricStage(
                updateModulesExecutionProcessMetricUseCase
            )
            val updateModulesDependencyGraphMetricStage = UpdateModulesDependencyGraphMetricStage(
                updateModulesDependencyGraphMetricUseCase
            )
            val updateModulesBuildHeatmapMetricStage = UpdateModulesBuildHeatmapMetricStage(
                updateModulesBuildHeatmapMetricUseCase
            )
            val updateDependencyDetailsMetricStage = UpdateDependencyDetailsMetricStage(
                updateDependencyDetailsMetricUseCase
            )

            val updatedMetric = UpdateMetricPipeline(updateInitializationProcessMetricStage)
                .addStage(updateConfigurationProcessMetricStage)
                .addStage(updateExecutionProcessMetricStage)
                .addStage(updateOverallBuildProcessMetricStage)
                .addStage(updateModulesSourceCountMetricStage)
                .addStage(updateModulesMethodCountMetricStage)
                .addStage(updateCacheHitMetricStage)
                .addStage(updateSuccessBuildRateMetricStage)
                .addStage(updateDependencyResolveProcessMetricStage)
                .addStage(updateParallelExecutionRateMetricStage)
                .addStage(updateModulesExecutionProcessMetricStage)
                .addStage(updateModulesDependencyGraphMetricStage)
                .addStage(updateModulesBuildHeatmapMetricStage)
                .addStage(updateDependencyDetailsMetricStage)
                .execute(BuildMetric(input.branch, input.requestedTasks, input.createdAt))

            val dayMetricNumber = repo.getDayMetric().second
            repo.updateDayMetric(dayMetricNumber, updatedMetric)

            return dayMetricNumber
        }

        return repo.saveNewMetric(input)
    }

}
