/**
 * MIT License
 * Copyright (c) 2022 Mehdi Janbarari (@janbarari)
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

class TaskInfoTest {

    @Test
    fun `check getDuration() returns correct result`() {
        val task = TaskInfo(
            1, 50, "assembleDebug", "assemble", "assemble", true, null, null, true, true, false, false, null
        )
        assertEquals(49, task.getDurationInMillis())
    }

    @Test
    fun `check getters`() {
        val task = TaskInfo(
            1, 50, "assembleDebug", "assemble", "assemble", true, null, null, true, true, false, false, null
        )
        assertEquals(1, task.startedAt)
        assertEquals(50, task.finishedAt)
        assertEquals("assembleDebug", task.path)
        assertEquals("assemble", task.displayName)
        assertEquals("assemble", task.name)
    }

    @Test
    fun `check getDuration() returns zero when finishedAt is smaller than startedAt`() {
        val task = TaskInfo(
            100, 50, "assembleDebug", "assemble", "assemble", true, null, null, true, true, false, false, null
        )
        assertEquals(0, task.getDurationInMillis())
    }

    @Test
    fun `check getModule() returns 'no_module'`() {
        val task = TaskInfo(
            100, 50, "assembleDebug", "assemble", "assemble", true, null, null, true, true, false, false, null
        )
        assertEquals("no_module", task.getModule())
    }

    @Test
    fun `check getModule() returns module`() {
        val task = TaskInfo(
            100, 50, ":feature:app:assembleDebug", "assemble", "assemble", true, null, null, true, true, false, false, null
        )
        assertEquals(":feature:app", task.getModule())
    }

}