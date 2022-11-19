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
    fun `when Collection#whenEach() invokes, validate iteration`() {
        val sampleData = listOf(
            "Woman",
            "Life",
            "Freedom"
        )
        var iterates = 0
        sampleData.whenEach {
            iterates++
        }
        assertEquals(3, iterates)
    }

    @Test
    fun `when toIntList() invoked, expect long list mapped to int list`() {
        val sampleData = listOf(0L, 1L, 10L, 100L)
        val convertedToInt = sampleData.toIntList()
        assertEquals(true, (convertedToInt[0] is Int))
    }

    @Test
    fun `when List#isBiggerThan() invoked, expect boolean result`() {
        val sampleData = listOf(0, 1, 2, 3, 4, 5)
        assertEquals(true, sampleData.isBiggerThan(3))
        assertEquals(false, sampleData.isBiggerThan(10))
    }

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
    fun `when 'firstIndex' is invoked, expect to get the first item index`() {
        val data = listOf(
            "Mehdi",
            "Shahram",
            "Shabnam"
        )
        assertEquals(data[data.firstIndex], "Mehdi")
    }

    @Test
    fun `when whenEmpty() is invoked, expect the lambda function called`() {
        val sampleData = listOf<String>()
        sampleData.whenEmpty {
            assert(true)
            return
        }
        assert(false)
    }

    @Test
    fun `when whenNotEmpty() is invoked, expect the lambda function called`() {
        val data = listOf<String>(
            "Woman",
            "Life",
            "Freedom"
        )
        data.whenNotEmpty {
            assert(true)
            return
        }
        assert(false)
    }

    @Test
    fun `when hasSingleItem() is invoked on a list with one item, expect true returned`() {
        val data = listOf<String>(
            "Mahsa Amini"
        )
        assertEquals(true, data.hasSingleItem())
    }

    @Test
    fun `when hasSingleItem() is invoked on a list with multiple items, expect false returned`() {
        val data = listOf<String>(
            "Woman",
            "Life",
            "Freedom"
        )
        assertEquals(false, data.hasSingleItem())
    }

    @Test
    fun `when hasMultipleItem() is invoked on a list with multiple items, expect true returned`() {
        val data = listOf<String>(
            "Woman",
            "Life",
            "Freedom"
        )
        assertEquals(true, data.hasMultipleItems())
    }

    @Test
    fun `when hasMultipleItem() is invoked on a list with one item, expect false returned`() {
        val data = listOf<String>(
            "Iran"
        )
        assertEquals(false, data.hasMultipleItems())
    }

    @Test
    fun `when List#toArrayRender() is invoked on a list with multiple items, validate the result`() {
        val data = listOf<String>(
            "Woman",
            "Life",
            "Freedom"
        )
        assertEquals(
            "[\"Woman\",\"Life\",\"Freedom\"]",
            data.toArrayRender()
        )
    }

    @Test
    fun `when List#toArrayRender() is invoked on an empty list, validate the result`() {
        val data = listOf<String>()
        assertEquals(
            "[]",
            data.toArrayRender()
        )
    }

}
