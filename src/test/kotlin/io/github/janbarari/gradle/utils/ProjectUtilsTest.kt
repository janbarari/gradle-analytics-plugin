package io.github.janbarari.gradle.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProjectUtilsTest {

    /**
     * Note: This test will be successful if the project Gradle version is above 6.1
     */
    @Test
    fun `Check isCompatibleWith() works fine when project gradle is above 6_1`() {
        val result = ProjectUtils.isCompatibleWith(ProjectUtils.GradleVersions.V6_1)
        assertEquals(true, result)
    }

    /**
     * Note: This test will be successful if the project Gradle version is below 7.4.1
     */
    @Test
    fun `Check isCompatibleWith() works fine when project gradle is below 7_4_2`() {
        val result = ProjectUtils.isCompatibleWith(ProjectUtils.GradleVersions.V7_4_2)
        assertEquals(false, result)
    }

    @Test
    fun `Check gradle version number returns correct value`() {
        assertEquals("6.1", ProjectUtils.GradleVersions.V6_1.versionNumber)
    }

    @Test
    fun `Check GradleUtils class instance creating`() {
        val instance = ProjectUtils()
        assert(true)
    }

}
