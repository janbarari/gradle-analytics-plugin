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

import com.squareup.moshi.Moshi
import io.github.janbarari.gradle.analytics.data.DatabaseRepositoryImp
import io.github.janbarari.gradle.analytics.database.Database
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.domain.usecase.GetMetricsUseCase
import io.github.janbarari.gradle.ExcludeJacocoGenerated
import io.github.janbarari.gradle.analytics.DatabaseConfig
import io.github.janbarari.gradle.analytics.GradleAnalyticsPlugin.Companion.OUTPUT_DIRECTORY_NAME
import io.github.janbarari.gradle.analytics.data.TemporaryMetricsMemoryCacheImpl
import io.github.janbarari.gradle.analytics.data.V100B6DatabaseResultMigrationStage
import io.github.janbarari.gradle.analytics.database.DatabaseResultMigrationPipeline
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetricJsonAdapter
import io.github.janbarari.gradle.analytics.domain.model.report.ModulesDependencyGraphReportJsonAdapter
import io.github.janbarari.gradle.analytics.domain.usecase.GetModulesTimelineUseCase
import io.github.janbarari.gradle.extension.isNull
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.logger.TowerImpl
import io.github.janbarari.gradle.memorycache.MemoryCache
import oshi.SystemInfo
import kotlin.io.path.Path

/**
 * Dependency injection for [io.github.janbarari.gradle.analytics.reporttask.ReportAnalyticsTask].
 */
@ExcludeJacocoGenerated
class ReportAnalyticsInjector(
    var branch: String? = null,
    var requestedTasks: String? = null,
    var isCI: Boolean? = null,
    var databaseConfig: DatabaseConfig? = null,
    var outputPath: String? = null,
    var projectName: String? = null,
    var modules: Set<String>? = null
) {

    // Singleton objects
    @Volatile
    var tower: Tower? = null

    @Volatile
    var moshi: Moshi? = null

    @Volatile
    var databaseRepository: DatabaseRepository? = null

    /**
     * Destroy singleton objects
     */
    fun destroy() {
        tower = null
        moshi = null
        databaseRepository = null
    }
}


@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideTower(): Tower {
    if (tower.isNull()) {
        tower = synchronized(this) {
            TowerImpl(
                name = "report",
                outputPath = Path("${outputPath!!}/${OUTPUT_DIRECTORY_NAME}"),
                shouldDropOldLogFile = true,
                maximumOldLogsCount = 0
            )
        }
    }
    return tower!!
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideSystemInfo(): SystemInfo {
    return SystemInfo()
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideDatabase(): Database {
    return Database(
        tower = provideTower(),
        config = databaseConfig!!,
        isCI = isCI!!
    )
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideMoshi(): Moshi {
    if (moshi.isNull()) {
        moshi = synchronized(this) {
            Moshi.Builder().build()
        }
    }
    return moshi!!
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideTemporaryMetricsMemoryCache(): MemoryCache<List<BuildMetric>> {
    return TemporaryMetricsMemoryCacheImpl(
        tower = provideTower()
    )
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideBuildMetricJsonAdapter(): BuildMetricJsonAdapter {
    return BuildMetricJsonAdapter(
        moshi = provideMoshi()
    )
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideDatabaseResultMigrationPipeline(): DatabaseResultMigrationPipeline {
    return DatabaseResultMigrationPipeline(V100B6DatabaseResultMigrationStage(modules = modules!!))
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideDatabaseRepository(): DatabaseRepository {
    if (databaseRepository.isNull()) {
        databaseRepository = synchronized(this) {
            DatabaseRepositoryImp(
                tower = provideTower(),
                db = provideDatabase(),
                branch = branch!!,
                requestedTasks = requestedTasks!!,
                buildMetricJsonAdapter = provideBuildMetricJsonAdapter(),
                temporaryMetricsMemoryCache = provideTemporaryMetricsMemoryCache(),
                databaseResultMigrationPipeline = provideDatabaseResultMigrationPipeline()
            )
        }
    }
    return databaseRepository!!
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideGetMetricsUseCase(): GetMetricsUseCase {
    return GetMetricsUseCase(
        repo = provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideGetModulesTimelineUseCase(): GetModulesTimelineUseCase {
    return GetModulesTimelineUseCase(
        moshi = provideMoshi(),
        repo = provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideModulesDependencyGraphReportJsonAdapter(): ModulesDependencyGraphReportJsonAdapter {
    return ModulesDependencyGraphReportJsonAdapter(provideMoshi())
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideReportAnalyticsLogic(): ReportAnalyticsLogic {
    return ReportAnalyticsLogicImp(
        tower = provideTower(),
        modulesDependencyGraphReportJsonAdapter = provideModulesDependencyGraphReportJsonAdapter(),
        getMetricsUseCase = provideGetMetricsUseCase(),
        getModulesTimelineUseCase = provideGetModulesTimelineUseCase(),
        isCI = isCI!!,
        outputPath = outputPath!!,
        projectName = projectName!!
    )
}
