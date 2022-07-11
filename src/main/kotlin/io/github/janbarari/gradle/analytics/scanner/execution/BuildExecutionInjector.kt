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
package io.github.janbarari.gradle.analytics.scanner.execution

import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig.DatabaseConfig
import io.github.janbarari.gradle.analytics.data.DatabaseRepositoryImp
import io.github.janbarari.gradle.analytics.data.database.Database
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.domain.usecase.SaveMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.SaveTemporaryMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.update.UpdateInitializationMetricUseCase
import io.github.janbarari.gradle.ExcludeJacocoGenerated
import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.metric.configuration.create.CreateConfigurationMetricUseCase
import io.github.janbarari.gradle.analytics.metric.configuration.update.UpdateConfigurationMetricUseCase
import io.github.janbarari.gradle.analytics.metric.execution.create.CreateExecutionMetricUseCase
import io.github.janbarari.gradle.analytics.metric.execution.update.UpdateExecutionMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.create.CreateInitializationMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.create.CreateModulesMethodCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.update.UpdateModulesMethodCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.create.CreateModulesSourceCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.update.UpdateModulesSourceCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.totalbuild.CreateTotalBuildMetricUseCase
import io.github.janbarari.gradle.analytics.metric.totalbuild.UpdateTotalBuildMetricUseCase
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.separateElementsWithSpace

/**
 * Custom dependency injector for [BuildExecutionLogic].
 */

@ExcludeJacocoGenerated
data class BuildExecutionInjector(
    var databaseConfig: DatabaseConfig? = null,
    var isCI: Boolean? = null,
    var branch: String? = null,
    var requestedTasks: List<String>? = null,
    var trackingBranches: List<String>? = null,
    var trackingTasks: List<String>? = null,
    var modulesPath: List<ModulePath>? = null
)

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideDatabase(): Database {
    return Database(ensureNotNull(databaseConfig), ensureNotNull(isCI))
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideDatabaseRepository(): DatabaseRepository {
    return DatabaseRepositoryImp(
        provideDatabase(),
        ensureNotNull(branch),
        ensureNotNull(requestedTasks).separateElementsWithSpace()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateInitializationMetricUseCase(): UpdateInitializationMetricUseCase {
    return UpdateInitializationMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateConfigurationMetricUseCase(): UpdateConfigurationMetricUseCase {
    return UpdateConfigurationMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateExecutionMetricUseCase(): UpdateExecutionMetricUseCase {
    return UpdateExecutionMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateTotalBuildMetricUseCase(): UpdateTotalBuildMetricUseCase {
    return UpdateTotalBuildMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesSourceCountMetricUseCase(): UpdateModulesSourceCountMetricUseCase {
    return UpdateModulesSourceCountMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesMethodCountMetricUseCase(): UpdateModulesMethodCountMetricUseCase {
    return UpdateModulesMethodCountMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideSaveMetricUseCase(): SaveMetricUseCase {
    return SaveMetricUseCase(
        provideDatabaseRepository(),
        provideUpdateInitializationMetricUseCase(),
        provideUpdateConfigurationMetricUseCase(),
        provideUpdateExecutionMetricUseCase(),
        provideUpdateTotalBuildMetricUseCase(),
        provideUpdateModulesSourceCountMetricUseCase(),
        provideUpdateModulesMethodCountMetricUseCase()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideSaveTemporaryMetricUseCase(): SaveTemporaryMetricUseCase {
    return SaveTemporaryMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateInitializationMetricUseCase(): CreateInitializationMetricUseCase {
    return CreateInitializationMetricUseCase()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateConfigurationMetricUseCase(): CreateConfigurationMetricUseCase {
    return CreateConfigurationMetricUseCase()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateExecutionMetricUseCase(): CreateExecutionMetricUseCase {
    return CreateExecutionMetricUseCase()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateTotalBuildMetricUseCase(): CreateTotalBuildMetricUseCase {
    return CreateTotalBuildMetricUseCase()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesSourceCountMetricUseCase(): CreateModulesSourceCountMetricUseCase {
    return CreateModulesSourceCountMetricUseCase()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesMethodCountMetricUseCase(): CreateModulesMethodCountMetricUseCase {
    return CreateModulesMethodCountMetricUseCase()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideBuildExecutionLogic(): BuildExecutionLogic {
    return BuildExecutionLogicImp(
        provideSaveMetricUseCase(),
        provideSaveTemporaryMetricUseCase(),
        provideCreateInitializationMetricUseCase(),
        provideCreateConfigurationMetricUseCase(),
        provideCreateExecutionMetricUseCase(),
        provideCreateTotalBuildMetricUseCase(),
        provideCreateModulesSourceCountMetricUseCase(),
        provideCreateModulesMethodCountMetricUseCase(),
        ensureNotNull(databaseConfig),
        ensureNotNull(isCI),
        ensureNotNull(trackingBranches),
        ensureNotNull(trackingTasks),
        ensureNotNull(requestedTasks),
        ensureNotNull(modulesPath)
    )
}
