package io.github.janbarari.gradle.analytics

import io.github.janbarari.gradle.analytics.domain.model.ModuleInfo
import io.github.janbarari.gradle.analytics.metric.modulesourcecount.CreateModulesSourceCountMetricUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TemporaryTest {

    @Test
    fun getListOfKotlinFiles() = runBlocking {
        val useCase = CreateModulesSourceCountMetricUseCase()
        val input = mutableListOf(
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/domain"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/data"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/list"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/feature/details"
            ),
            ModuleInfo(
                path = ":path",
                absoluteDir = "/Users/workstation/Workstation/Github/janbarari/SatellitesTracker/app"
            )
        )

        println("Pre size = ${input.size}")
        println("After size = ${useCase.execute(input).modules.size}")
    }

}
