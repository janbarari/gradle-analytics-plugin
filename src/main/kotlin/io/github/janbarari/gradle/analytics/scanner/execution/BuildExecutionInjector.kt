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
import io.github.janbarari.gradle.ExcludeJacocoGenerated
import io.github.janbarari.gradle.analytics.GradleAnalyticsPlugin.Companion.OUTPUT_DIRECTORY_NAME
import io.github.janbarari.gradle.analytics.data.DatabaseRepositoryImp
import io.github.janbarari.gradle.analytics.data.TemporaryMetricsMemoryCacheImpl
import io.github.janbarari.gradle.analytics.data.V100B6DatabaseResultMigrationStage
import io.github.janbarari.gradle.analytics.database.Database
import io.github.janbarari.gradle.analytics.database.DatabaseResultMigrationPipeline
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetricJsonAdapter
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.domain.usecase.SaveMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.SaveTemporaryMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.UpsertModulesTimelineUseCase
import io.github.janbarari.gradle.analytics.metric.cachehit.create.CreateCacheHitMetricUseCase
import io.github.janbarari.gradle.analytics.metric.cachehit.update.UpdateCacheHitMetricUseCase
import io.github.janbarari.gradle.analytics.metric.configurationprocess.create.CreateConfigurationProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.configurationprocess.update.UpdateConfigurationProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.dependencyresolveprocess.create.CreateDependencyResolveProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.dependencyresolveprocess.update.UpdateDependencyResolveProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.executionprocess.create.CreateExecutionProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.executionprocess.update.UpdateExecutionProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initializationprocess.create.CreateInitializationProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initializationprocess.update.UpdateInitializationProcessMetricUseCase
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
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.create.CreateOverallBuildProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.overallbuildprocess.update.UpdateOverallBuildProcessMetricUseCase
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.create.CreateParallelExecutionRateMetricUseCase
import io.github.janbarari.gradle.analytics.metric.paralleexecutionrate.update.UpdateParallelExecutionRateMetricUseCase
import io.github.janbarari.gradle.analytics.metric.successbuildrate.create.CreateSuccessBuildRateMetricUseCase
import io.github.janbarari.gradle.analytics.metric.successbuildrate.update.UpdateSuccessBuildRateMetricUseCase
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.extension.separateElementsWithSpace
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.logger.TowerImpl
import io.github.janbarari.gradle.memorycache.MemoryCache
import kotlin.io.path.Path

/**
 * Dependency injector for [io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionLogic].
 */
@ExcludeJacocoGenerated
data class BuildExecutionInjector(
    val parameters: BuildExecutionService.Params
) {
    // Singleton objects
    @Volatile
    var tower: Tower? = null

    @Volatile
    var moshi: Moshi? = null

    @Volatile
    var currentBranch: String? = null

    @Volatile
    var databaseRepository: DatabaseRepository? = null

    /**
     * Destroy singleton objects
     */
    fun destroy() {
        tower = null
        moshi = null
        currentBranch = null
        databaseRepository = null
    }
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCurrentBranch(): String {
    if (currentBranch.isNull()) {
        currentBranch = synchronized(this) {
            parameters.gitCurrentBranch.get()
        }
    }
    return currentBranch!!
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideTower(): Tower {
    if (tower.isNull()) {
        tower = synchronized(this) {
            TowerImpl(
                name = "build",
                outputPath = Path("${parameters.outputPath.get()}/$OUTPUT_DIRECTORY_NAME"),
                shouldDropOldLogFile = false,
                maximumOldLogsCount = 500
            )
        }
    }
    return tower!!
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideDatabase(): Database {
    return Database(
        provideTower(),
        parameters.databaseConfig.get(),
        parameters.envCI.get()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideMoshi(): Moshi {
    if (moshi.isNull()) {
        moshi = synchronized(this) {
            Moshi.Builder().build()
        }
    }
    return moshi!!
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideTemporaryMetricsMemoryCache(): MemoryCache<List<BuildMetric>> {
    return TemporaryMetricsMemoryCacheImpl(
        tower = provideTower()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideBuildMetricJsonAdapter(): BuildMetricJsonAdapter {
    return BuildMetricJsonAdapter(provideMoshi())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideDatabaseResultMigrationPipeline(): DatabaseResultMigrationPipeline {
    return DatabaseResultMigrationPipeline(
        V100B6DatabaseResultMigrationStage(
            modules = parameters.modules.get().map { it.path }.toSet()
        )
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideDatabaseRepository(): DatabaseRepository {
    if (databaseRepository.isNull()) {
        databaseRepository = synchronized(this) {
            DatabaseRepositoryImp(
                tower = provideTower(),
                db = provideDatabase(),
                branch = provideCurrentBranch(),
                requestedTasks = parameters.requestedTasks.get().separateElementsWithSpace(),
                buildMetricJsonAdapter = provideBuildMetricJsonAdapter(),
                temporaryMetricsMemoryCache = provideTemporaryMetricsMemoryCache(),
                databaseResultMigrationPipeline = provideDatabaseResultMigrationPipeline()
            )
        }
    }
    return databaseRepository!!
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
    return UpdateDependencyResolveProcessMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateParallelExecutionRateMetricUseCase(): UpdateParallelExecutionRateMetricUseCase {
    return UpdateParallelExecutionRateMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesExecutionProcessMetricUseCase(): UpdateModulesExecutionProcessMetricUseCase {
    return UpdateModulesExecutionProcessMetricUseCase(
        provideTower(),
        provideDatabaseRepository(),
        parameters.modules.get()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesDependencyGraphMetricUseCase(): UpdateModulesDependencyGraphMetricUseCase {
    return UpdateModulesDependencyGraphMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesBuildHeatmapMetricUseCase(): UpdateModulesBuildHeatmapMetricUseCase {
    return UpdateModulesBuildHeatmapMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateNonCacheableTasksMetricUseCase(): UpdateNonCacheableTasksMetricUseCase {
    return UpdateNonCacheableTasksMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesSourceSizeMetricUseCase(): UpdateModulesSourceSizeMetricUseCase {
    return UpdateModulesSourceSizeMetricUseCase(
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpdateModulesCrashCountMetricUseCase(): UpdateModulesCrashCountMetricUseCase {
    return UpdateModulesCrashCountMetricUseCase(
        provideTower(),
        provideDatabaseRepository(),
        parameters.modules.get()
    )
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
        provideTower(),
        provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideUpsertModulesTimelineUseCase(): UpsertModulesTimelineUseCase {
    return UpsertModulesTimelineUseCase(
        tower = provideTower(),
        moshi = provideMoshi(),
        repo = provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateInitializationProcessMetricUseCase(): CreateInitializationProcessMetricUseCase {
    return CreateInitializationProcessMetricUseCase(
        provideTower()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateConfigurationProcessMetricUseCase(): CreateConfigurationProcessMetricUseCase {
    return CreateConfigurationProcessMetricUseCase(
        provideTower()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateExecutionProcessMetricUseCase(): CreateExecutionProcessMetricUseCase {
    return CreateExecutionProcessMetricUseCase(
        provideTower()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateOverallBuildProcessMetricUseCase(): CreateOverallBuildProcessMetricUseCase {
    return CreateOverallBuildProcessMetricUseCase(
        provideTower()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesSourceCountMetricUseCase(): CreateModulesSourceCountMetricUseCase {
    return CreateModulesSourceCountMetricUseCase(
        tower = provideTower(),
        modules = parameters.modules.get()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesMethodCountMetricUseCase(): CreateModulesMethodCountMetricUseCase {
    return CreateModulesMethodCountMetricUseCase(
        tower = provideTower(),
        modules = parameters.modules.get()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateCacheHitMetricUseCase(): CreateCacheHitMetricUseCase {
    return CreateCacheHitMetricUseCase(
        tower = provideTower(),
        modules = parameters.modules.get()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateSuccessBuildRateMetricUseCase(): CreateSuccessBuildRateMetricUseCase {
    return CreateSuccessBuildRateMetricUseCase(
        provideTower()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateDependencyResolveProcessMetricUseCase(): CreateDependencyResolveProcessMetricUseCase {
    return CreateDependencyResolveProcessMetricUseCase(
        provideTower()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateParallelExecutionRateMetricUseCase(): CreateParallelExecutionRateMetricUseCase {
    return CreateParallelExecutionRateMetricUseCase(
        provideTower()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesExecutionProcessMetricUseCase(): CreateModulesExecutionProcessMetricUseCase {
    return CreateModulesExecutionProcessMetricUseCase(
        tower = provideTower(),
        modules = parameters.modules.get()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesDependencyGraphMetricUseCase(): CreateModulesDependencyGraphMetricUseCase {
    return CreateModulesDependencyGraphMetricUseCase(
        tower = provideTower(),
        modulesDependencyGraph = parameters.modulesDependencyGraph.get()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesTimelineMetricUseCase(): CreateModulesTimelineMetricUseCase {
    return CreateModulesTimelineMetricUseCase(
        tower = provideTower(),
        modules = parameters.modules.get()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesBuildHeatmapMetricUseCase(): CreateModulesBuildHeatmapMetricUseCase {
    return CreateModulesBuildHeatmapMetricUseCase(
        tower = provideTower(),
        modules = parameters.modules.get(),
        modulesDependencyGraph = parameters.modulesDependencyGraph.get()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateNonCacheableTasksMetricUseCase(): CreateNonCacheableTasksMetricUseCase {
    return CreateNonCacheableTasksMetricUseCase(
        tower = provideTower(),
        nonCacheableTasks = parameters.nonCacheableTasks.get()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesSourceSizeMetricUseCase(): CreateModulesSourceSizeMetricUseCase {
    return CreateModulesSourceSizeMetricUseCase(
        tower = provideTower(),
        modules = parameters.modules.get()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideCreateModulesCrashCountMetricUseCase(): CreateModulesCrashCountMetricUseCase {
    return CreateModulesCrashCountMetricUseCase(
        tower = provideTower(),
        modules = parameters.modules.get()
    )
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideBuildExecutionLogic(): BuildExecutionLogic {
    return BuildExecutionLogicImp(
        tower = provideTower(),
        requestedTasks = parameters.requestedTasks.get(),
        modules = parameters.modules.get(),
        maximumWorkerCount = parameters.maximumWorkerCount.get(),
        gitCurrentBranch = parameters.gitCurrentBranch.get(),
        gitHeadCommitHash = parameters.gitHeadCommitHash.get(),
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
