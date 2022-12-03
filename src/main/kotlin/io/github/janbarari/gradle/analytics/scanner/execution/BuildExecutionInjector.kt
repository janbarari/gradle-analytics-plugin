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

import com.squareup.moshi.Moshi
import io.github.janbarari.gradle.analytics.data.DatabaseRepositoryImp
import io.github.janbarari.gradle.analytics.database.Database
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.domain.usecase.SaveMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.SaveTemporaryMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initializationprocess.update.UpdateInitializationProcessMetricUseCase
import io.github.janbarari.gradle.ExcludeJacocoGenerated
import io.github.janbarari.gradle.analytics.DatabaseConfig
import io.github.janbarari.gradle.analytics.domain.model.Module
import io.github.janbarari.gradle.analytics.domain.model.ModulesDependencyGraph
import io.github.janbarari.gradle.analytics.domain.usecase.UpsertModulesTimelineUseCase
import io.github.janbarari.gradle.analytics.metric.successbuildrate.create.CreateSuccessBuildRateMetricUseCase
import io.github.janbarari.gradle.analytics.metric.successbuildrate.update.UpdateSuccessBuildRateMetricUseCase
import io.github.janbarari.gradle.analytics.metric.cachehit.create.CreateCacheHitMetricUseCase
import io.github.janbarari.gradle.analytics.metric.cachehit.update.UpdateCacheHitMetricUseCase
import io.github.janbarari.gradle.analytics.metric.configurationprocess.create.CreateConfigurationProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.configurationprocess.update.UpdateConfigurationProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.dependencyresolveprocess.create.CreateDependencyResolveProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.dependencyresolveprocess.update.UpdateDependencyResolveProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.executionprocess.create.CreateExecutionProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.executionprocess.update.UpdateExecutionProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initializationprocess.create.CreateInitializationProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesbuildheatmap.create.CreateModulesBuildHeatmapMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesbuildheatmap.update.UpdateModulesBuildHeatmapMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulescrashcount.create.CreateModulesCrashCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulescrashcount.update.UpdateModulesCrashCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesdependencygraph.create.CreateModulesDependencyGraphMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesdependencygraph.update.UpdateModulesDependencyGraphMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.create.CreateModulesExecutionProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesexecutionprocess.update.UpdateModulesExecutionProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.create.CreateModulesMethodCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesmethodcount.update.UpdateModulesMethodCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.create.CreateModulesSourceCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.update.UpdateModulesSourceCountMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulessourcesize.create.CreateModulesSourceSizeMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulessourcesize.update.UpdateModulesSourceSizeMetricUseCase
import io.github.janbarari.gradle.analytics.metric.modulestimeline.create.CreateModulesTimelineMetricUseCase
import io.github.janbarari.gradle.analytics.metric.noncacheabletasks.create.CreateNonCacheableTasksMetricUseCase
import io.github.janbarari.gradle.analytics.metric.noncacheabletasks.update.UpdateNonCacheableTasksMetricUseCase
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.create.CreateParallelExecutionRateMetricUseCase
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.update.UpdateParallelExecutionRateMetricUseCase
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.create.CreateOverallBuildProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.update.UpdateOverallBuildProcessMetricUseCase
import io.github.janbarari.gradle.extension.separateElementsWithSpace

/**
 * Dependency injector for [io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionLogic].
 */
@ExcludeJacocoGenerated
data class BuildExecutionInjector(
    var databaseConfig: DatabaseConfig? = null,
    var isCI: Boolean? = null,
    var branch: String? = null,
    var requestedTasks: List<String>? = null,
    var trackingBranches: List<String>? = null,
    var trackingTasks: List<String>? = null,
    var modules: List<Module>? = null,
    var modulesDependencyGraph: ModulesDependencyGraph? = null,
    var nonCacheableTasks: List<String>? = null
)

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideDatabase(): Database {
    return Database(databaseConfig!!, isCI!!)
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideMoshi(): Moshi {
    return Moshi.Builder().build()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideDatabaseRepository(): DatabaseRepository {
    return DatabaseRepositoryImp(
        db = provideDatabase(),
        branch = branch!!,
        requestedTasks = requestedTasks!!.separateElementsWithSpace(),
        moshi = provideMoshi()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateInitializationMetricUseCase(): UpdateInitializationProcessMetricUseCase {
    return UpdateInitializationProcessMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateConfigurationMetricUseCase(): UpdateConfigurationProcessMetricUseCase {
    return UpdateConfigurationProcessMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateExecutionProcessMetricUseCase(): UpdateExecutionProcessMetricUseCase {
    return UpdateExecutionProcessMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateOverallBuildProcessMetricUseCase(): UpdateOverallBuildProcessMetricUseCase {
    return UpdateOverallBuildProcessMetricUseCase(provideDatabaseRepository())
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
fun BuildExecutionInjector.provideUpdateCacheHitMetricUseCase(): UpdateCacheHitMetricUseCase {
    return UpdateCacheHitMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateSuccessBuildRateMetricUseCase(): UpdateSuccessBuildRateMetricUseCase {
    return UpdateSuccessBuildRateMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateDependencyResolveProcessMetricUseCase(): UpdateDependencyResolveProcessMetricUseCase {
    return UpdateDependencyResolveProcessMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateParallelExecutionRateMetricUseCase(): UpdateParallelExecutionRateMetricUseCase {
    return UpdateParallelExecutionRateMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesExecutionProcessMetricUseCase(): UpdateModulesExecutionProcessMetricUseCase {
    return UpdateModulesExecutionProcessMetricUseCase(provideDatabaseRepository(), modules!!)
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesDependencyGraphMetricUseCase(): UpdateModulesDependencyGraphMetricUseCase {
    return UpdateModulesDependencyGraphMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesBuildHeatmapMetricUseCase(): UpdateModulesBuildHeatmapMetricUseCase {
    return UpdateModulesBuildHeatmapMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateNonCacheableTasksMetricUseCase(): UpdateNonCacheableTasksMetricUseCase {
    return UpdateNonCacheableTasksMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesSourceSizeMetricUseCase(): UpdateModulesSourceSizeMetricUseCase {
    return UpdateModulesSourceSizeMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesCrashCountMetricUseCase(): UpdateModulesCrashCountMetricUseCase {
    return UpdateModulesCrashCountMetricUseCase(provideDatabaseRepository(), modules!!)
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideSaveMetricUseCase(): SaveMetricUseCase {
    return SaveMetricUseCase(
        provideDatabaseRepository(),
        provideUpdateInitializationMetricUseCase(),
        provideUpdateConfigurationMetricUseCase(),
        provideUpdateExecutionProcessMetricUseCase(),
        provideUpdateOverallBuildProcessMetricUseCase(),
        provideUpdateModulesSourceCountMetricUseCase(),
        provideUpdateModulesMethodCountMetricUseCase(),
        provideUpdateCacheHitMetricUseCase(),
        provideUpdateSuccessBuildRateMetricUseCase(),
        provideUpdateDependencyResolveProcessMetricUseCase(),
        provideUpdateParallelExecutionRateMetricUseCase(),
        provideUpdateModulesExecutionProcessMetricUseCase(),
        provideUpdateModulesDependencyGraphMetricUseCase(),
        provideUpdateModulesBuildHeatmapMetricUseCase(),
        provideUpdateNonCacheableTasksMetricUseCase(),
        provideUpdateModulesSourceSizeMetricUseCase(),
        provideUpdateModulesCrashCountMetricUseCase()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideSaveTemporaryMetricUseCase(): SaveTemporaryMetricUseCase {
    return SaveTemporaryMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpsertModulesTimelineUseCase(): UpsertModulesTimelineUseCase {
    return UpsertModulesTimelineUseCase(
        moshi = provideMoshi(),
        repo = provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateInitializationProcessMetricUseCase(): CreateInitializationProcessMetricUseCase {
    return CreateInitializationProcessMetricUseCase()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateConfigurationProcessMetricUseCase(): CreateConfigurationProcessMetricUseCase {
    return CreateConfigurationProcessMetricUseCase()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateExecutionProcessMetricUseCase(): CreateExecutionProcessMetricUseCase {
    return CreateExecutionProcessMetricUseCase()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateOverallBuildProcessMetricUseCase(): CreateOverallBuildProcessMetricUseCase {
    return CreateOverallBuildProcessMetricUseCase()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesSourceCountMetricUseCase(): CreateModulesSourceCountMetricUseCase {
    return CreateModulesSourceCountMetricUseCase(modules!!)
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesMethodCountMetricUseCase(): CreateModulesMethodCountMetricUseCase {
    return CreateModulesMethodCountMetricUseCase(modules!!)
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateCacheHitMetricUseCase(): CreateCacheHitMetricUseCase {
    return CreateCacheHitMetricUseCase(modules!!)
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateSuccessBuildRateMetricUseCase(): CreateSuccessBuildRateMetricUseCase {
    return CreateSuccessBuildRateMetricUseCase()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateDependencyResolveProcessMetricUseCase(): CreateDependencyResolveProcessMetricUseCase {
    return CreateDependencyResolveProcessMetricUseCase()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateParallelExecutionRateMetricUseCase(): CreateParallelExecutionRateMetricUseCase {
    return CreateParallelExecutionRateMetricUseCase()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesExecutionProcessMetricUseCase(): CreateModulesExecutionProcessMetricUseCase {
    return CreateModulesExecutionProcessMetricUseCase(modules!!)
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesDependencyGraphMetricUseCase(): CreateModulesDependencyGraphMetricUseCase {
    return CreateModulesDependencyGraphMetricUseCase(modulesDependencyGraph!!)
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesTimelineMetricUseCase(): CreateModulesTimelineMetricUseCase {
    return CreateModulesTimelineMetricUseCase(modules!!)
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesBuildHeatmapMetricUseCase(): CreateModulesBuildHeatmapMetricUseCase {
    return CreateModulesBuildHeatmapMetricUseCase(modules!!, modulesDependencyGraph!!)
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateNonCacheableTasksMetricUseCase(): CreateNonCacheableTasksMetricUseCase {
    return CreateNonCacheableTasksMetricUseCase(nonCacheableTasks!!)
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesSourceSizeMetricUseCase(): CreateModulesSourceSizeMetricUseCase {
    return CreateModulesSourceSizeMetricUseCase(modules!!)
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesCrashCountMetricUseCase(): CreateModulesCrashCountMetricUseCase {
    return CreateModulesCrashCountMetricUseCase(modules!!)
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideBuildExecutionLogic(): BuildExecutionLogic {
    return BuildExecutionLogicImp(
        requestedTasks = requestedTasks!!,
        modules = modules!!,
        saveMetricUseCase = provideSaveMetricUseCase(),
        saveTemporaryMetricUseCase = provideSaveTemporaryMetricUseCase(),
        upsertModulesTimelineUseCase = provideUpsertModulesTimelineUseCase(),
        provideCreateInitializationProcessMetricUseCase(),
        provideCreateConfigurationProcessMetricUseCase(),
        provideCreateExecutionProcessMetricUseCase(),
        provideCreateOverallBuildProcessMetricUseCase(),
        provideCreateModulesSourceCountMetricUseCase(),
        provideCreateModulesMethodCountMetricUseCase(),
        provideCreateCacheHitMetricUseCase(),
        provideCreateSuccessBuildRateMetricUseCase(),
        provideCreateDependencyResolveProcessMetricUseCase(),
        provideCreateParallelExecutionRateMetricUseCase(),
        provideCreateModulesExecutionProcessMetricUseCase(),
        provideCreateModulesDependencyGraphMetricUseCase(),
        provideCreateModulesTimelineMetricUseCase(),
        provideCreateModulesBuildHeatmapMetricUseCase(),
        provideCreateNonCacheableTasksMetricUseCase(),
        provideCreateModulesSourceSizeMetricUseCase(),
        provideCreateModulesCrashCountMetricUseCase(),
    )
}
