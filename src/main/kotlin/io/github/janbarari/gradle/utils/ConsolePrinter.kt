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
package io.github.janbarari.gradle.utils

/**
 * Prints the messages in the terminal console in a visual format.
 */
class ConsolePrinter(private var blockCharWidth: Int) {

    init {
        blockCharWidth += 1
    }

    fun printFirstLine(firstSpace: Boolean = true) {
        val output = StringBuilder()
        if (firstSpace)
            output.append("\n")
        output.append(" ")
        output.append("┌")
        (0..blockCharWidth).forEach {
            output.append("─")
        }
        output.append("┐")
        println(output.toString())
    }

    fun printLastLine() {
        val output = StringBuilder()
        output.append(" ")
        output.append("└")
        (0..blockCharWidth).forEach { _ ->
            output.append("─")
        }
        output.append("┘")
        println(output.toString())
    }

    fun printLine(left: String = "", right: String = "") {
        val output = StringBuilder()
        output.append(" ")
        output.append("│")
        output.append(" ")
        output.append(left)
        val remainingEmptyCharactersCount = blockCharWidth - left.length - right.length - 1
        (0 until remainingEmptyCharactersCount).forEach { _ ->
            output.append(" ")
        }
        output.append(right)
        output.append(" ")
        output.append("│")
        println(output.toString())
    }

    fun printBreakLine(char: Char = '─') {
        val output = StringBuilder()
        output.append(" ")
        output.append("│")
        (0..blockCharWidth).forEach { _ ->
            output.append(char)
        }
        output.append("│")
        println(output.toString())
    }

}
