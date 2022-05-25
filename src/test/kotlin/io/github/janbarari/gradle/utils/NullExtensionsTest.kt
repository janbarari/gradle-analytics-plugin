package io.github.janbarari.gradle.utils

import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.extension.isNull
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

    @Test
    fun `check isNotNull function returns true if object is not null`() {
        val nullObj: String = "This variable is not null"
        assertEquals(true, nullObj.isNotNull())
    }

    @Test
    fun `check isNotNull function returns false if object is null`() {
        val nullObj: String? = null
        assertEquals(false, nullObj.isNotNull())
    }

}
