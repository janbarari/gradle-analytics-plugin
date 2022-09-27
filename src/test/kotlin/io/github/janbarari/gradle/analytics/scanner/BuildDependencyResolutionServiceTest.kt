package io.github.janbarari.gradle.analytics.scanner

import io.github.janbarari.gradle.analytics.domain.model.DependencyResolveInfo
import io.github.janbarari.gradle.analytics.scanner.dependencyresolution.BuildDependencyResolutionService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuildDependencyResolutionServiceTest {

    lateinit var service: BuildDependencyResolutionService

    @BeforeAll
    fun setup() {
        service = BuildDependencyResolutionService()
    }

    @Test
    fun `test reset() function removes the dependency resolutions`() {
        BuildDependencyResolutionService.dependenciesResolveInfo.set(
            "A", DependencyResolveInfo(
                ":clean",
                startedAt = 1,
                finishedAt = 2
            )
        )
        BuildDependencyResolutionService.reset()
        assertEquals(0, BuildDependencyResolutionService.dependenciesResolveInfo.size)
    }

}