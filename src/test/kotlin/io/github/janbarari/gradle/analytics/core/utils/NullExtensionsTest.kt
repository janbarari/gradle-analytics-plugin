package io.github.janbarari.gradle.analytics.core.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NullExtensionsTest {

    @Test
    fun `check isNull function returns true if object is null`() {
        val nullObj: String? = null
        assertEquals(true, nullObj.isNull())
    }

    @Test
    fun `check isNull function returns false if object is not null`() {
        val nullObj: String = "This variable is not null"
        assertEquals(false, nullObj.isNull())
    }

}
