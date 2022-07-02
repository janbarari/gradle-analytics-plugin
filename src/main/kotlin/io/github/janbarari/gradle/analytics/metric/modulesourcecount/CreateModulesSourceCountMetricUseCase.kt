package io.github.janbarari.gradle.analytics.metric.modulesourcecount

import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.model.ModuleSourceCount
import io.github.janbarari.gradle.analytics.domain.model.ModulesSourceCountMetric
import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.extension.whenEach
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.pathString

class CreateModulesSourceCountMetricUseCase : UseCase<List<ModulePath>, ModulesSourceCountMetric>() {

    override suspend fun execute(input: List<ModulePath>): ModulesSourceCountMetric {
        val modulesProperties = mutableListOf<ModuleSourceCount>()
        withContext(dispatcher) {
            val defers = mutableListOf<Deferred<Boolean>>()
            input.whenEach {
                defers.add(async {
                    modulesProperties.add(
                        ModuleSourceCount(
                            path = path,
                            value = getModuleSources(absoluteDir).size
                        )
                    )
                })
            }
            defers.forEach { it.await() }
        }
        return ModulesSourceCountMetric(modules = modulesProperties)
    }

    fun getModuleSources(directory: String): List<Path> {
        var sourcePaths: List<Path>
        Files.walk(Path(directory)).use { stream ->
            sourcePaths = stream.map { obj: Path -> obj.normalize() }
                .filter(Files::isRegularFile)
                .filter {
                    (it.pathString.contains("src/main/java") ||
                            it.pathString.contains("src/main/kotlin")) &&
                            (it.extension == "kt" || it.extension == "java")
                }.collect(Collectors.toList())
        }
        return sourcePaths
    }

}
