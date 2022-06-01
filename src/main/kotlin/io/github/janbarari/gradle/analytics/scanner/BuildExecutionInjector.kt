package io.github.janbarari.gradle.analytics.scanner

import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig.DatabaseConfig
import io.github.janbarari.gradle.analytics.data.DatabaseRepositoryImp
import io.github.janbarari.gradle.analytics.data.database.Database
import io.github.janbarari.gradle.analytics.domain.repository.DatabaseRepository
import io.github.janbarari.gradle.analytics.domain.usecase.SaveMetricUseCase
import io.github.janbarari.gradle.analytics.domain.usecase.SaveTemporaryMetricUseCase
import io.github.janbarari.gradle.analytics.metric.initialization.InitializationMetricMedianUseCase
import io.github.janbarari.gradle.extension.ExcludeJacocoGenerated
import io.github.janbarari.gradle.extension.ensureNotNull
import io.github.janbarari.gradle.extension.separateElementsWithSpace

@ExcludeJacocoGenerated
data class BuildExecutionInjector(
    var databaseConfig: DatabaseConfig? = null,
    var isCI: Boolean? = null,
    var branch: String? = null,
    var requestedTasks: List<String>? = null,
    var trackingBranches: List<String>? = null,
    var trackingTasks: List<String>? = null
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
fun BuildExecutionInjector.provideInitializationMetricMedianUseCase(): InitializationMetricMedianUseCase {
    return InitializationMetricMedianUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideSaveMetricUseCase(): SaveMetricUseCase {
    return SaveMetricUseCase(provideDatabaseRepository(), provideInitializationMetricMedianUseCase())
}

@ExcludeJacocoGenerated
fun BuildExecutionInjector.provideSaveTemporaryMetricUseCase(): SaveTemporaryMetricUseCase {
    return SaveTemporaryMetricUseCase(provideDatabaseRepository())
}

@ExcludeJacocoGenerated
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
