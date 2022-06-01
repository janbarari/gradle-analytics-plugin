package io.github.janbarari.gradle.analytics.scanner

import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig.DatabaseConfig
import io.github.janbarari.gradle.analytics.data.DatabaseRepositoryImp
import io.github.janbarari.gradle.analytics.data.database.Database
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.domain.usecase.SaveMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.SaveTemporaryMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.InitializationMetricMedianUseCase
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.separateElementsWithSpace

data class BuildExecutionInjector(
    var databaseConfig: DatabaseConfig? = null,
    var isCI: Boolean? = null,
    var branch: String? = null,
    var requestedTasks: List<String>? = null,
    var trackingBranches: List<String>? = null,
    var trackingTasks: List<String>? = null
)

fun BuildExecutionInjector.provideDatabase(): Database {
    return Database(ensureNotNull(databaseConfig), ensureNotNull(isCI))
}
fun BuildExecutionInjector.provideDatabaseRepository(): DatabaseRepository {
    return DatabaseRepositoryImp(
        provideDatabase(),
        ensureNotNull(branch),
        ensureNotNull(requestedTasks).separateElementsWithSpace()
    )
}

fun BuildExecutionInjector.provideInitializationMetricMedianUseCase(): InitializationMetricMedianUseCase {
    return InitializationMetricMedianUseCase(provideDatabaseRepository())
}

fun BuildExecutionInjector.provideSaveMetricUseCase(): SaveMetricUseCase {
    return SaveMetricUseCase(provideDatabaseRepository(), provideInitializationMetricMedianUseCase())
}

fun BuildExecutionInjector.provideSaveTemporaryMetricUseCase(): SaveTemporaryMetricUseCase {
    return SaveTemporaryMetricUseCase(provideDatabaseRepository())
}

fun BuildExecutionInjector.provideBuildExecutionLogic(): BuildExecutionLogic {
    return BuildExecutionLogicImp(
        provideSaveMetricUseCase(),
        provideSaveTemporaryMetricUseCase(),
        ensureNotNull(databaseConfig),
        ensureNotNull(isCI),
        ensureNotNull(trackingBranches),
        ensureNotNull(trackingTasks),
        ensureNotNull(requestedTasks)
    )
}
