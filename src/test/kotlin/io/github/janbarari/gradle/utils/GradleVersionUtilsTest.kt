package io.github.janbarari.gradle.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GradleVersionUtilsTest {

    /**
     * Note: This test will be successful if the test runner Gradle version is above 6.1
     */
    @Test
    fun `test isCompatibleWith function works fine when current gradle is above 6_1`() {
        val result = GradleVersionUtils.isCompatibleWith(GradleVersionUtils.GradleVersions.V6_1)
        assertEquals(true, result)
    }

    /**
     * Note: This test will be successful if the test runner Gradle version is under 7.4.1
     */
    @Test
    fun `test isCompatibleWith function works fine when current gradle is under 7_4_2`() {
        val result = GradleVersionUtils.isCompatibleWith(GradleVersionUtils.GradleVersions.V7_4_2)
        assertEquals(false, result)
    }

    @Test
    fun `test gradle version number returns correct value`() {
        assertEquals("6.1", GradleVersionUtils.GradleVersions.V6_1.versionNumber)
    }

}