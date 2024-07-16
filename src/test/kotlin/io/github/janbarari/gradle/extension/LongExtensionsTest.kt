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

class LongExtensionsTest {

    @Test
    fun `when Long#isZero() invoked, validate the result`() {
        assertEquals(true, 0L.isZero())
        assertEquals(false, 1L.isZero())
    }


    @Test
    fun `when Long#isBiggerEquals() invoked with bigger input, expect true result`() {
        assertEquals(true, 10L.isBiggerEquals(9))
    }

    @Test
    fun `when Long#isBiggerEquals() invoked with same input, expect true result`() {
        assertEquals(true, 10L.isBiggerEquals(10))
    }

    @Test
    fun `when Long#isBiggerEquals() invoked with smaller input, expect false result`() {
        assertEquals(false, 0L.isBiggerEquals(10))
    }


    @Test
    fun `when Long#isBigger() invoked with bigger input, expect true result`() {
        assertEquals(true, 10L.isBigger(8))
    }

    @Test
    fun `when Long#isBigger() invoked with smaller input, expect false result`() {
        assertEquals(false, 0L.isBigger(10))
    }


    @Test
    fun `when Long#millisToSeconds() invoked, validate the result`() {
        assertEquals(1L, 1000L.millisToSeconds())
    }

}
