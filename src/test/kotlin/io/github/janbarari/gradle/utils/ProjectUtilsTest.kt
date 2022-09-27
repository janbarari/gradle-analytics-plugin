package io.github.janbarari.gradle.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProjectUtilsTest {

    @Test
    fun `check isCompatibleWith() returns true when the version is above 7`() {
        val result = ProjectUtils.isCompatibleWith(ProjectUtils.GradleVersions.V6_1)
        assertEquals(true, result)
    }

    @Test
    fun `check isCompatibleWith() returns true when the version is before 7_5`() {
        val result = ProjectUtils.isCompatibleWith(ProjectUtils.GradleVersions.V7_5)
        assertEquals(false, result)
    }

}