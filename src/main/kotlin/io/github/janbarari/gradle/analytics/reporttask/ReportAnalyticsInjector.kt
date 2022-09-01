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
import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig.DatabaseConfig
import io.github.janbarari.gradle.analytics.data.DatabaseRepositoryImp
import io.github.janbarari.gradle.analytics.data.database.Database
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.domain.usecase.GetMetricsUseCase
import io.github.janbarari.gradle.ExcludeJacocoGenerated
import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.usecase.GetModulesTimelineUseCase
import io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionInjector
import io.github.janbarari.gradle.extension.ensureNotNull

/**
 * Dependency injection for [ReportAnalyticsTask].
 */
@ExcludeJacocoGenerated
class ReportAnalyticsInjector(
    var branch: String? = null,
    var requestedTasks: String? = null,
    var isCI: Boolean? = null,
    var databaseConfig: DatabaseConfig? = null,
    var outputPath: String? = null,
    var projectName: String? = null,
    var modulesPath: List<ModulePath>? = null
)

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideDatabase(): Database {
    return Database(ensureNotNull(databaseConfig), ensureNotNull(isCI))
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideMoshi(): Moshi {
    return Moshi.Builder().build()
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideDatabaseRepository(): DatabaseRepository {
    return DatabaseRepositoryImp(
        provideDatabase(),
        ensureNotNull(branch),
        ensureNotNull(requestedTasks)
    )
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideGetMetricsUseCase(): GetMetricsUseCase {
    return GetMetricsUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideGetModulesTimelineUseCase(): GetModulesTimelineUseCase {
    return GetModulesTimelineUseCase(
        moshi = provideMoshi(),
        repo = provideDatabaseRepository()
    )
}

@ExcludeJacocoGenerated
fun ReportAnalyticsInjector.provideReportAnalyticsLogic(): ReportAnalyticsLogic {
    return ReportAnalyticsLogicImp(
        provideGetMetricsUseCase(),
        provideGetModulesTimelineUseCase(),
        ensureNotNull(isCI),
        ensureNotNull(outputPath),
        ensureNotNull(projectName),
        ensureNotNull(modulesPath)
    )
}
