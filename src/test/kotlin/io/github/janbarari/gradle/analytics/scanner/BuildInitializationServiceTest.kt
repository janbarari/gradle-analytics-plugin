package io.github.janbarari.gradle.analytics.scanner

import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuildInitializationServiceTest {

    private lateinit var service: BuildInitializationService

    @BeforeAll
    fun setup() {
        service = BuildInitializationService(mockk())
    }

    @Test
    fun `check reset() function clears the STARTED_AT & INITIALIZED_AT`() {
        BuildInitializationService.STARTED_AT = 38
        BuildInitializationService.reset()

        assertEquals(0, BuildInitializationService.STARTED_AT)
        assertEquals(0, BuildInitializationService.INITIALIZED_AT)
    }

    @Test
    fun `check beforeEvaluate() assigns the INITIALIZED_AT if its not initialized`() {
        service.beforeEvaluate(mockk())
        assert(BuildInitializationService.INITIALIZED_AT > 0)

        BuildInitializationService.INITIALIZED_AT = 10L
        service.beforeEvaluate(mockk())
        assert(BuildInitializationService.INITIALIZED_AT == 10L)
    }

    @Test
    fun `check afterEvaluate() assigns the INITIALIZED_AT if its not initialized`() {
        service.afterEvaluate(mockk(), mockk())
        assert(BuildInitializationService.INITIALIZED_AT > 0)

        BuildInitializationService.INITIALIZED_AT = 10L
        service.afterEvaluate(mockk(), mockk())
        assert(BuildInitializationService.INITIALIZED_AT == 10L)
    }

}