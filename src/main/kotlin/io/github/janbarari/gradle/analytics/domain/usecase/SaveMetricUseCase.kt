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

import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.analytics.domain.model.BuildMetric
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.metric.configuration.UpdateConfigurationMetricStage
import io.github.janbarari.gradle.analytics.metric.configuration.UpdateConfigurationMetricUseCase
import io.github.janbarari.gradle.analytics.metric.execution.UpdateExecutionMetricStage
import io.github.janbarari.gradle.analytics.metric.execution.UpdateExecutionMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.UpdateInitializationMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.UpdateInitializationMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.CreateModulesSourceCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.UpdateModulesSourceCountMetricStage
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.UpdateModulesSourceCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.totalbuild.UpdateTotalBuildMetricStage
import io.github.janbarari.gradle.analytics.metric.totalbuild.UpdateTotalBuildMetricUseCase

class SaveMetricUseCase(
    private val repo: DatabaseRepository,
    private val updateInitializationMetricUseCase: UpdateInitializationMetricUseCase,
    private val updateConfigurationMetricUseCase: UpdateConfigurationMetricUseCase,
    private val updateExecutionMetricUseCase: UpdateExecutionMetricUseCase,
    private val updateTotalBuildMetricUseCase: UpdateTotalBuildMetricUseCase,
    private val updateModulesSourceCountMetricUseCase: UpdateModulesSourceCountMetricUseCase
): UseCase<BuildMetric, Long>() {

    override suspend fun execute(input: BuildMetric): Long {

        if (repo.isDayMetricExists()) {

            val updateInitializationMetricStage = UpdateInitializationMetricStage(updateInitializationMetricUseCase)
            val updateConfigurationMetricStage = UpdateConfigurationMetricStage(updateConfigurationMetricUseCase)
            val updateExecutionMetricStage = UpdateExecutionMetricStage(updateExecutionMetricUseCase)
            val updateTotalBuildMetricStage = UpdateTotalBuildMetricStage(updateTotalBuildMetricUseCase)
            val updateModulesSourceCountMetricStage =
                UpdateModulesSourceCountMetricStage(updateModulesSourceCountMetricUseCase)

            val updatedMetric = UpdateMetricPipeline(updateInitializationMetricStage)
                .addStage(updateConfigurationMetricStage)
                .addStage(updateExecutionMetricStage)
                .addStage(updateTotalBuildMetricStage)
                .addStage(updateModulesSourceCountMetricStage)
                .execute(BuildMetric(input.branch, input.requestedTasks, input.createdAt))

            val dayMetricNumber = repo.getDayMetric().second

            repo.updateDayMetric(dayMetricNumber, updatedMetric)
            return dayMetricNumber
        }

        return repo.saveNewMetric(input)
    }

}
