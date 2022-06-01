package io.github.janbarari.gradle.analytics.scanner

import io.github.janbarari.gradle.FakeListProperty
import io.github.janbarari.gradle.FakeProperty
import io.github.janbarari.gradle.analytics.GradleAnalyticsPluginConfig
import io.github.janbarari.gradle.analytics.domain.model.TaskInfo
import io.github.janbarari.gradle.analytics.domain.usecase.SaveTemporaryMetricUseCase
import io.github.janbarari.gradle.utils.GitUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkObject
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.internal.impldep.org.eclipse.jgit.api.Git
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuildExecutionServiceTest {

    data class Param(
        val databaseConfig: GradleAnalyticsPluginConfig.DatabaseConfig? = null,
        val envCI: Boolean? = null,
        val requestedTasks: List<String>? = null,
        val trackingTasks: List<String>? = null,
        val trackingBranches: List<String>? = null
    )

    private fun createService(param: Param): BuildExecutionService {
        return object :BuildExecutionService() {
            override fun getParameters(): Params = object :Params {
                override val databaseConfig: Property<GradleAnalyticsPluginConfig.DatabaseConfig> = FakeProperty(param.databaseConfig)
                override val envCI: Property<Boolean> = FakeProperty(param.envCI)
                override val requestedTasks: ListProperty<String> = FakeListProperty(param.requestedTasks)
                override val trackingTasks: ListProperty<String> = FakeListProperty(param.trackingTasks)
                override val trackingBranches: ListProperty<String> = FakeListProperty(param.trackingBranches)
            }
        }
    }
}