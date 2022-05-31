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

    @Test
    fun `check isBranchTrackable() returns false when branch is not trackable`() {
        val service = createService(
            Param(
                trackingBranches = listOf("develop", "master")
            )
        )

        mockkObject(GitUtils)
        every { GitUtils.currentBranch() } returns "feature-1"

        assertEquals(false, service.isBranchTrackable())
    }

    @Test
    fun `check isBranchTrackable() returns true when branch is trackable`() {
        val service = createService(
            Param(
                trackingBranches = listOf("develop", "master")
            )
        )

        mockkObject(GitUtils)
        every { GitUtils.currentBranch() } returns "develop"

        assertEquals(true, service.isBranchTrackable())
    }

    @Test
    fun `check isTaskTrackable() returns false when requestedTask is not trackable`() {
        val service = createService(
            Param(
                trackingTasks = listOf(":app:assembleDebug"),
                requestedTasks = listOf(":app:clean")
            )
        )

        assertEquals(false, service.isTaskTrackable())
    }

    @Test
    fun `check isTaskTrackable() returns true when requestedTask is not trackable`() {
        val service = createService(
            Param(
                trackingTasks = listOf(":app:assembleDebug"),
                requestedTasks = listOf(":app:assembleDebug")
            )
        )

        assertEquals(true, service.isTaskTrackable())
    }

    @Test
    fun `check isForbiddenTasksRequested() returns true when reportAnalytics task requested`() {
        val service = createService(
            Param(
                requestedTasks = listOf(":app:clean", "reportAnalytics")
            )
        )

        assertEquals(true, service.isForbiddenTasksRequested())
    }

    @Test
    fun `check isDatabaseConfigurationValid() returns false when local machine config is not set and ran on local`() {
        val service = createService(
            Param(
                databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
                    local = null
                    ci = null
                },
                envCI = false
            )
        )

        assertEquals(false, service.isDatabaseConfigurationValid())
    }

    @Test
    fun `check isDatabaseConfigurationValid() returns false when ci machine config is not set and ran on CI`() {
        val service = createService(
            Param(
                databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
                    local = null
                    ci = null
                },
                envCI = true
            )
        )

        assertEquals(false, service.isDatabaseConfigurationValid())
    }

    @Test
    fun `check isDatabaseConfigurationValid() returns false when local machine config is set and ran on CI`() {
        val service = createService(
            Param(
                databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
                    local = sqlite {  }
                    ci = null
                },
                envCI = true
            )
        )

        assertEquals(false, service.isDatabaseConfigurationValid())
    }

    @Test
    fun `check isDatabaseConfigurationValid() returns false when ci machine config is set and ran on Local`() {
        val service = createService(
            Param(
                databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
                    local = null
                    ci = mysql {  }
                },
                envCI = false
            )
        )

        assertEquals(false, service.isDatabaseConfigurationValid())
    }

    @Test
    fun `check isDatabaseConfigurationValid() returns true when ci machine config is set and ran on CI`() {
        val service = createService(
            Param(
                databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
                    local = null
                    ci = mysql {  }
                },
                envCI = true
            )
        )

        assertEquals(true, service.isDatabaseConfigurationValid())
    }

    @Test
    fun `check isDatabaseConfigurationValid() returns true when Local machine config is set and ran on Local`() {
        val service = createService(
            Param(
                databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
                    local = sqlite {  }
                    ci = null
                },
                envCI = false
            )
        )

        assertEquals(true, service.isDatabaseConfigurationValid())
    }

    @Test
    fun `check onExecutionFinished() return false when reportAnalytics task requested`() {
        val service = createService(
            Param(
                requestedTasks = listOf("reportAnalytics")
            )
        )

        val fakeExecutedTasks = listOf<TaskInfo>(
            TaskInfo(10L, 50L, ":app:assembleDebug", "task #1", "task #1"),
            TaskInfo(0L, 10L, ":app:assembleDebug", "task #2", "task #2"),
            TaskInfo(30L, 70L, ":app:assembleDebug", "task #3", "task #3"),
            TaskInfo(20L, 80L, ":app:assembleDebug", "task #4", "task #4"),
            TaskInfo(44L, 52L, ":app:assembleDebug", "task #5", "task #5")
        )

        assertEquals(false, service.onExecutionFinished(fakeExecutedTasks))
    }

    @Test
    fun `check onExecutionFinished() return false when database configuration is not valid`() {
        val service = createService(
            Param(
                requestedTasks = listOf(":app:assembleDebug"),
                databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
                    local = null
                },
                envCI = false
            )
        )

        val fakeExecutedTasks = listOf(
            TaskInfo(10L, 50L, ":app:assembleDebug", "task #1", "task #1"),
            TaskInfo(0L, 10L, ":app:assembleDebug", "task #2", "task #2"),
            TaskInfo(30L, 70L, ":app:assembleDebug", "task #3", "task #3"),
            TaskInfo(20L, 80L, ":app:assembleDebug", "task #4", "task #4"),
            TaskInfo(44L, 52L, ":app:assembleDebug", "task #5", "task #5")
        )

        assertEquals(false, service.onExecutionFinished(fakeExecutedTasks))
    }

    @Test
    fun `check oExecutionFinished() return false when requestedTasks are not trackable`() {
        val service = createService(
            Param(
                requestedTasks = listOf(":app:assembleDebug"),
                databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
                    local = null
                },
                envCI = false,
                trackingTasks = listOf("clean"),
                trackingBranches = listOf("remote", "master")
            )
        )

        val fakeExecutedTasks = listOf(
            TaskInfo(10L, 50L, ":app:assembleDebug", "task #1", "task #1"),
            TaskInfo(0L, 10L, ":app:assembleDebug", "task #2", "task #2"),
            TaskInfo(30L, 70L, ":app:assembleDebug", "task #3", "task #3"),
            TaskInfo(20L, 80L, ":app:assembleDebug", "task #4", "task #4"),
            TaskInfo(44L, 52L, ":app:assembleDebug", "task #5", "task #5")
        )

        assertEquals(false, service.onExecutionFinished(fakeExecutedTasks))
    }

    @Test
    fun `check oExecutionFinished() return false when branch is not trackable`() {
        val service = createService(
            Param(
                requestedTasks = listOf(":app:assembleDebug"),
                databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
                    local = null
                },
                envCI = false,
                trackingTasks = listOf(":app:assembleDebug"),
                trackingBranches = listOf("remote", "master")
            )
        )

        val fakeExecutedTasks = listOf(
            TaskInfo(10L, 50L, ":app:assembleDebug", "task #1", "task #1"),
            TaskInfo(0L, 10L, ":app:assembleDebug", "task #2", "task #2"),
            TaskInfo(30L, 70L, ":app:assembleDebug", "task #3", "task #3"),
            TaskInfo(20L, 80L, ":app:assembleDebug", "task #4", "task #4"),
            TaskInfo(44L, 52L, ":app:assembleDebug", "task #5", "task #5")
        )

        mockkObject(GitUtils)
        every { GitUtils.currentBranch() } returns "feature-1"

        assertEquals(false, service.onExecutionFinished(fakeExecutedTasks))
    }

    @Test
    fun `check oExecutionFinished() return true when save process was successful`() {
        val service = createService(
            Param(
                requestedTasks = listOf(":app:assembleDebug"),
                databaseConfig = GradleAnalyticsPluginConfig.DatabaseConfig().apply {
                    local = sqlite {
                        path = "./"
                    }
                },
                envCI = false,
                trackingTasks = listOf(":app:assembleDebug", ":app:assembleRelease"),
                trackingBranches = listOf("develop", "master")
            )
        )

        val fakeExecutedTasks = listOf(
            TaskInfo(10L, 50L, ":app:assembleDebug", "task #1", "task #1"),
            TaskInfo(0L, 10L, ":app:assembleDebug", "task #2", "task #2"),
            TaskInfo(30L, 70L, ":app:assembleDebug", "task #3", "task #3"),
            TaskInfo(20L, 80L, ":app:assembleDebug", "task #4", "task #4"),
            TaskInfo(44L, 52L, ":app:assembleDebug", "task #5", "task #5")
        )

        mockkObject(GitUtils)
        every { GitUtils.currentBranch() } returns "develop"

        assertEquals(true, service.onExecutionFinished(fakeExecutedTasks))
    }

}