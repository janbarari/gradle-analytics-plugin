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
package io.github.janbarari.gradle.extension

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ListExtensionsTest {

    @Test
    fun `when the 'modify' operator invoke, expect the changes in final result`() {
        data class TestModel(val name: String, var age: Int)
        val data = listOf(
            TestModel("Mehdi", 26),
            TestModel("Shahram", 27),
            TestModel("Shabnam", 30)
        )
        data
            .filter { it.name == "Shabnam" }
            .modify {
                age = 31
            }
        assertEquals(data[2].age, 31)
    }

    @Test
    fun `when 'firstIndex' invoked, expect to get the first item index`() {
        val data = listOf(
            "Mehdi",
            "Shahram",
            "Shabnam"
        )
        assertEquals(data[data.firstIndex], "Mehdi")
    }

}
