package io.github.janbarari.gradle.analytics

import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.CreateModulesSourceCountMetricUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TemporaryTest {

    @Test
    fun getListOfKotlinFiles() = runBlocking {
        val useCase = CreateModulesSourceCountMetricUseCase()
        val input = mutableListOf(
            ModulePath(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            )
        )
        println(useCase.execute(input).modules.toString())
    }

}
