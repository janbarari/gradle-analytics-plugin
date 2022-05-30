package io.github.janbarari.gradle.analytics.scanner

import io.mockk.mockk
import org.gradle.BuildResult
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuildConfigurationServiceTest {

    lateinit var service: BuildConfigurationService
    private val mockedGradle = mockk<Gradle>()
    private val mockedSettings = mockk<Settings>()
    private val mockedBuildResult = mockk<BuildResult>()

    @BeforeAll
    fun setup() {
        service = BuildConfigurationService()
    }

    @Test
    fun `check reset() function clears the CONFIGURED_AT`() {
        BuildConfigurationService.CONFIGURED_AT = 1804
        BuildConfigurationService.reset()
        assertEquals(0, BuildConfigurationService.CONFIGURED_AT)
    }

    @Test
    fun `check settingsEvaluated() function won't return any exception`() {
        service.settingsEvaluated(mockedSettings)
        assert(true)
    }

    @Test
    fun `check projectsLoaded() function won't return any exception`() {
        service.projectsLoaded(mockedGradle)
        assert(true)
    }

    @Test
    fun `check projectsEvaluated() updates the CONFIGURED_AT`() {
        BuildConfigurationService.CONFIGURED_AT = 0L
        service.projectsEvaluated(mockedGradle)
        assert(BuildConfigurationService.CONFIGURED_AT > 0)
    }

    @Test
    fun `check buildFinished() function won't return any exception`() {
        service.buildFinished(mockedBuildResult)
        assert(true)
    }

}