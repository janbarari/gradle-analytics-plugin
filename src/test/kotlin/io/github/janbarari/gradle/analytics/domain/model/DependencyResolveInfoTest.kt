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
package io.github.janbarari.gradle.analytics.domain.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DependencyResolveInfoTest {

    @Test
    fun `check getDuration() returns correct result`() {
        val dri = DependencyResolveInfo(
            "assembleDebug", 0, 1600
        )
        assertEquals(1600, dri.getDuration())
    }

    @Test
    fun `check getDuration() returns zero when startedAt and finishedAt not set`() {
        val dri = DependencyResolveInfo(
            "assembleDebug", startedAt = 0L
        )
        assertEquals(0, dri.getDuration())
    }

    @Test
    fun `check getDuration() returns zero when finishedAt not set`() {
        val dri = DependencyResolveInfo(
            "assembleDebug", startedAt = 100
        )
        assertEquals(0, dri.getDuration())
    }

    @Test
    fun `check getters`() {
        val dri = DependencyResolveInfo(
            "assembleDebug", startedAt = 100
        )
        assertEquals(100, dri.startedAt)
        assertEquals(0, dri.finishedAt)
        assertEquals("assembleDebug", dri.path)
    }

    @Test
    fun `check setters`() {
        val dri = DependencyResolveInfo(
            "assembleDebug", startedAt = 100
        )
        dri.finishedAt = 10
        assertEquals(10, dri.finishedAt)
    }

}