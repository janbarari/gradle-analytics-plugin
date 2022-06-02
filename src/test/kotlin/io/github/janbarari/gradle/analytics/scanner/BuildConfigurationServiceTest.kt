package io.github.janbarari.gradle.analytics.scanner

import io.github.janbarari.gradle.analytics.scanner.configuration.BuildConfigurationService
import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuildConfigurationServiceTest {

    lateinit var service: BuildConfigurationService

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
    fun `check projectsEvaluated() updates the CONFIGURED_AT`() {
        BuildConfigurationService.CONFIGURED_AT = 0L
        service.projectsEvaluated(mockk())
        assert(BuildConfigurationService.CONFIGURED_AT > 0)
    }

}