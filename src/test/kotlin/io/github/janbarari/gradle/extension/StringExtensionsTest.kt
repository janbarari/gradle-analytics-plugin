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

class StringExtensionsTest {

    @Test
    fun `check toFilePath() returns correct path`() {
        val path1 = "/Users/woman/life/freedom/"
        assertEquals("/Users/woman/life/freedom", path1.toRealPath())
        val path2 = "/Users/woman/life/freedom//"
        assertEquals("/Users/woman/life/freedom", path2.toRealPath())
        val path3 = "/Users/woman/life/freedom///"
        assertEquals("/Users/woman/life/freedom", path3.toRealPath())
    }

    @Test
    fun `when removeLastChar() invoked, expect remove last character`() {
        val temp = java.lang.StringBuilder()
        temp.append("Hello World!")
        temp.removeLastChar()
        assertEquals("Hello World", temp.toString())
    }

    @Test
    fun `when removeLastChar() invoked on empty string, expect remove last character`() {
        val temp = java.lang.StringBuilder()
        temp.removeLastChar()
        assertEquals("", temp.toString())
    }

    @Test
    fun `when separateElementsWithSpace() invoked, expect a string with separated items`() {
        val sampleData = listOf(
            "woman",
            "life",
            "freedom"
        )
        assertEquals("woman life freedom", sampleData.separateElementsWithSpace())
    }

    @Test
    fun `check string has space`() {
        assertEquals(true, "Hello World".hasSpace())
        assertEquals(false, "HelloWorld".hasSpace())
    }

    @Test
    fun `when removeLastChar() invoked, expect remove last character`() {
        val temp = java.lang.StringBuilder()
        temp.append("Hello World!")
        temp.removeLastChar()
        assertEquals("Hello World", temp.toString())
    }

    @Test
    fun `when removeLastChar() invoked on empty string, expect remove last character`() {
        val temp = java.lang.StringBuilder()
        temp.removeLastChar()
        assertEquals("", temp.toString())
    }

    @Test
    fun `when separateElementsWithSpace() invoked, expect a string with separated items`() {
        val sampleData = listOf(
            "woman",
            "life",
            "freedom"
        )
        assertEquals("woman life freedom", sampleData.separateElementsWithSpace())
    }

    @Test
    fun `check string has space`() {
        assertEquals(true, "Hello World".hasSpace())
        assertEquals(false, "HelloWorld".hasSpace())
    }

}