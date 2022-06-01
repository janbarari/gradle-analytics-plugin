package io.github.janbarari.gradle.extension

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NullExtensionsTest {

    @Test
    fun `check isNull() return false when object is not null`() {
        val obj = "Hello World!"
        assertEquals(false, obj.isNull())
    }

    @Test
    fun `check isNull() return true when object is null`() {
        val obj = null
        assertEquals(true, obj.isNull())
    }

    @Test
    fun `check isNotNull() return true when object is not null`() {
        val obj = "Hello World!"
        assertEquals(true, obj.isNotNull())
    }

    @Test
    fun `check isNull() return false when object is null`() {
        val obj = null
        assertEquals(false, obj.isNotNull())
    }

    @Test
    fun `check whenNotNull() when object not null`() {
        val obj = "Hello World"
        obj.whenNotNull {
            assertEquals("Hello World", this)
        }
    }

    @Test
    fun `check whenNotNull() when object is null`() {
        val obj = null
        obj.whenNotNull {
        }
        assert(true)
    }

    @Test
    fun `check whenNull() when object is null`() {
        val obj = null
        obj.whenNull {
            assertEquals(null, null)
        }
    }

    @Test
    fun `check whenNull() when object is not null`() {
        val obj = "Hello World"
        obj.whenNull {
        }
        assert(true)
    }

}