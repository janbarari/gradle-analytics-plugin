/**
 * MIT License
 * Copyright (c) 2024 Mehdi Janbarari (@janbarari)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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