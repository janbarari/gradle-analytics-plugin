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
import io.github.janbarari.gradle.analytics.metric.buildsuccessratio.update.UpdateBuildSuccessRatioMetricStage
import io.github.janbarari.gradle.analytics.metric.buildsuccessratio.update.UpdateBuildSuccessRatioMetricUseCase
import io.github.janbarari.gradle.analytics.metric.cachehit.update.UpdateCacheHitMetricStage
import io.github.janbarari.gradle.analytics.metric.cachehit.update.UpdateCacheHitMetricUseCase
import io.github.janbarari.gradle.analytics.metric.configuration.update.UpdateConfigurationMetricStage
import io.github.janbarari.gradle.analytics.metric.configuration.update.UpdateConfigurationMetricUseCase
import io.github.janbarari.gradle.analytics.metric.dependencyresolvemetric.update.UpdateDependencyResolveMetricStage
import io.github.janbarari.gradle.analytics.metric.dependencyresolvemetric.update.UpdateDependencyResolveMetricUseCase
import io.github.janbarari.gradle.analytics.metric.execution.update.UpdateExecutionMetricStage
import io.github.janbarari.gradle.analytics.metric.execution.update.UpdateExecutionMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.update.UpdateInitializationMetricStage
import io.github.janbarari.gradle.analytics.metric.initialization.update.UpdateInitializationMetricUseCase
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
    private val updateInitializationMetricUseCase: UpdateInitializationMetricUseCase,
    private val updateConfigurationMetricUseCase: UpdateConfigurationMetricUseCase,
    private val updateExecutionMetricUseCase: UpdateExecutionMetricUseCase,
    private val updateOverallBuildProcessMetricUseCase: UpdateOverallBuildProcessMetricUseCase,
    private val updateModulesSourceCountMetricUseCase: UpdateModulesSourceCountMetricUseCase,
    private val updateModulesMethodCountMetricUseCase: UpdateModulesMethodCountMetricUseCase,
    private val updateCacheHitMetricUseCase: UpdateCacheHitMetricUseCase,
    private val updateBuildSuccessRatioMetricUseCase: UpdateBuildSuccessRatioMetricUseCase,
    private val updateDependencyResolveMetricUseCase: UpdateDependencyResolveMetricUseCase,
    private val updateParallelExecutionRateMetricUseCase: UpdateParallelExecutionRateMetricUseCase
) : UseCase<BuildMetric, Long>() {

    /**
     * Saves/Updates the daily metric in the database.
     */
    override suspend fun execute(input: BuildMetric): Long {

        if (repo.isDayMetricExists()) {

            val updateInitializationMetricStage = UpdateInitializationMetricStage(updateInitializationMetricUseCase)
            val updateConfigurationMetricStage = UpdateConfigurationMetricStage(updateConfigurationMetricUseCase)
            val updateExecutionMetricStage = UpdateExecutionMetricStage(updateExecutionMetricUseCase)
            val updateOverallBuildProcessMetricStage =
                UpdateOverallBuildProcessMetricStage(updateOverallBuildProcessMetricUseCase)
            val updateModulesSourceCountMetricStage = UpdateModulesSourceCountMetricStage(updateModulesSourceCountMetricUseCase)
            val updateModulesMethodCountMetricStage = UpdateModulesMethodCountMetricStage(updateModulesMethodCountMetricUseCase)
            val updateCacheHitMetricStage = UpdateCacheHitMetricStage(updateCacheHitMetricUseCase)
            val updateBuildSuccessRatioMetricStage = UpdateBuildSuccessRatioMetricStage(updateBuildSuccessRatioMetricUseCase)
            val updateDependencyResolveMetricStage = UpdateDependencyResolveMetricStage(updateDependencyResolveMetricUseCase)
            val updateParallelExecutionRateMetricStage = UpdateParallelExecutionRateMetricStage(updateParallelExecutionRateMetricUseCase)


            val updatedMetric = UpdateMetricPipeline(updateInitializationMetricStage)
                .addStage(updateConfigurationMetricStage)
                .addStage(updateExecutionMetricStage)
                .addStage(updateOverallBuildProcessMetricStage)
                .addStage(updateModulesSourceCountMetricStage)
                .addStage(updateModulesMethodCountMetricStage)
                .addStage(updateCacheHitMetricStage)
                .addStage(updateBuildSuccessRatioMetricStage)
                .addStage(updateDependencyResolveMetricStage)
                .addStage(updateParallelExecutionRateMetricStage)
                .execute(BuildMetric(input.branch, input.requestedTasks, input.createdAt))

            val dayMetricNumber = repo.getDayMetric().second
            repo.updateDayMetric(dayMetricNumber, updatedMetric)

            return dayMetricNumber
        }

        return repo.saveNewMetric(input)
    }

}
