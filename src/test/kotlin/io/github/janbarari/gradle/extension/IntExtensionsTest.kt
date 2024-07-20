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

class IntExtensionsTest {

    @Test
    fun `when (Int) diffPercentageOf() invoked, validate the result`() {
        val first = 400
        val last = 589
        assertEquals(47.25f, first.diffPercentageOf(last))
    }

    @Test
    fun `when (Int) 0#diffPercentageOf() invoked, expect zero as result`() {
        assertEquals(0f, 0.diffPercentageOf(10))
    }

    @Test
    fun `when (Long) diffPercentageOf() invoked, validate the result`() {
        val first = 400L
        val last = 589L
        assertEquals(47.25f, first.diffPercentageOf(last))
    }

    @Test
    fun `when (Long) 0#diffPercentageOf() invoked, expect zero as result`() {
        assertEquals(0f, 0L.diffPercentageOf(10))
    }

    @Test
    fun `when (Int) toPercentageOf() invoked, validate the result`() {
        val first = 5
        val target = 62
        println(first.toPercentageOf(target))
        assertEquals(8.06f, first.toPercentageOf(target))
    }

    @Test
    fun `when (Int) toPercentageOf() invoked with target zero, expect zero as result`() {
        val first = 5
        val target = 0
        println(first.toPercentageOf(target))
        assertEquals(0f, first.toPercentageOf(target))
    }

    @Test
    fun `when (Long) toPercentageOf() invoked, validate the result`() {
        val first = 5L
        val target = 62L
        println(first.toPercentageOf(target))
        assertEquals(8.06f, first.toPercentageOf(target))
    }

    @Test
    fun `when (Long) toPercentageOf() invoked with target zero, expect zero as result`() {
        val first = 5L
        val target = 0L
        println(first.toPercentageOf(target))
        assertEquals(0f, first.toPercentageOf(target))
    }

}
