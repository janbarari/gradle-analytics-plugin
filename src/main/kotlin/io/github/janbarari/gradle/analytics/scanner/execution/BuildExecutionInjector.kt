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
import io.github.janbarari.gradle.analytics.GradleAnalyticsPlugin.Companion.OUTPUT_DIRECTORY_NAME
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
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.separateElementsWithSpace
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.logger.TowerImpl
import io.github.janbarari.gradle.utils.GitUtils
import oshi.SystemInfo
import kotlin.io.path.Path

/**
 * Dependency injector for [io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionLogic].
 */
@ExcludeJacocoGenerated
data class BuildExecutionInjector(
    val parameters: BuildExecutionService.Params
)

// Singleton instances
var tower: Tower? = null
var moshi: Moshi? = null
var database: Database? = null

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideSystemInfo(): SystemInfo {
    return SystemInfo()
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideTower(): Tower {
    if (tower.isNull()) {
        tower = TowerImpl(
            name = "build",
            outputPath = Path("${parameters.outputPath.get()}/$OUTPUT_DIRECTORY_NAME"),
            shouldDropOldLogFile = false,
            maximumOldLogsCount = 500
        )
    }
    return tower!!
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideDatabase(): Database {
    if (database.isNull()) {
        database = Database(
            provideTower(),
            parameters.databaseConfig.get(),
            parameters.envCI.get()
        )
    }
    return database!!
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideMoshi(): Moshi {
    if (moshi.isNull()) {
        moshi = Moshi.Builder().build()
    }
    return moshi!!
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideDatabaseRepository(): DatabaseRepository {
    return DatabaseRepositoryImp(
        db = provideDatabase(),
        branch = GitUtils.currentBranch(),
        requestedTasks = parameters.requestedTasks.get().separateElementsWithSpace(),
        moshi = provideMoshi(),
        tower = provideTower()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateInitializationMetricUseCase(): UpdateInitializationProcessMetricUseCase {
    return UpdateInitializationProcessMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateConfigurationMetricUseCase(): UpdateConfigurationProcessMetricUseCase {
    return UpdateConfigurationProcessMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateExecutionProcessMetricUseCase(): UpdateExecutionProcessMetricUseCase {
    return UpdateExecutionProcessMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateOverallBuildProcessMetricUseCase(): UpdateOverallBuildProcessMetricUseCase {
    return UpdateOverallBuildProcessMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesSourceCountMetricUseCase(): UpdateModulesSourceCountMetricUseCase {
    return UpdateModulesSourceCountMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesMethodCountMetricUseCase(): UpdateModulesMethodCountMetricUseCase {
    return UpdateModulesMethodCountMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateCacheHitMetricUseCase(): UpdateCacheHitMetricUseCase {
    return UpdateCacheHitMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateSuccessBuildRateMetricUseCase(): UpdateSuccessBuildRateMetricUseCase {
    return UpdateSuccessBuildRateMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
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
    return UpdateModulesExecutionProcessMetricUseCase(provideDatabaseRepository(), parameters.modules.get())
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
    return UpdateModulesCrashCountMetricUseCase(provideDatabaseRepository(), parameters.modules.get())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideSaveMetricUseCase(): SaveMetricUseCase {
    return SaveMetricUseCase(
        provideTower(),
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
    return SaveTemporaryMetricUseCase(
        provideDatabaseRepository(),
        provideTower()
    )
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
    return CreateModulesSourceCountMetricUseCase(parameters.modules.get())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesMethodCountMetricUseCase(): CreateModulesMethodCountMetricUseCase {
    return CreateModulesMethodCountMetricUseCase(parameters.modules.get())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateCacheHitMetricUseCase(): CreateCacheHitMetricUseCase {
    return CreateCacheHitMetricUseCase(parameters.modules.get())
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
    return CreateModulesExecutionProcessMetricUseCase(parameters.modules.get())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesDependencyGraphMetricUseCase(): CreateModulesDependencyGraphMetricUseCase {
    return CreateModulesDependencyGraphMetricUseCase(parameters.modulesDependencyGraph.get())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesTimelineMetricUseCase(): CreateModulesTimelineMetricUseCase {
    return CreateModulesTimelineMetricUseCase(parameters.modules.get())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesBuildHeatmapMetricUseCase(): CreateModulesBuildHeatmapMetricUseCase {
    return CreateModulesBuildHeatmapMetricUseCase(parameters.modules.get(), parameters.modulesDependencyGraph.get())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateNonCacheableTasksMetricUseCase(): CreateNonCacheableTasksMetricUseCase {
    return CreateNonCacheableTasksMetricUseCase(parameters.nonCacheableTasks.get())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesSourceSizeMetricUseCase(): CreateModulesSourceSizeMetricUseCase {
    return CreateModulesSourceSizeMetricUseCase(parameters.modules.get())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesCrashCountMetricUseCase(): CreateModulesCrashCountMetricUseCase {
    return CreateModulesCrashCountMetricUseCase(parameters.modules.get())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideBuildExecutionLogic(): BuildExecutionLogic {
    return BuildExecutionLogicImp(
        tower = provideTower(),
        requestedTasks = parameters.requestedTasks.get(),
        modules = parameters.modules.get(),
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

/**
 * Destroy singleton instances
 */
fun BuildExecutionInjector.destroy() {
    tower = null
    moshi = null
    database = null
}
