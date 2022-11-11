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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.extension

/**
 * Invoke function body when the value is true.
 *
 * This function helps to reduce the code complexity and increase the development speed by removing the
 * boilerplate if condition for booleans.
 */
fun Boolean.whenTrue(block: Boolean.() -> Unit) {
    if (this) block(true)
}

/**
 * Invoke function body when the value is false.
 *
 * This function helps to reduce the code complexity and increase the development speed by removing the
 * boilerplate if condition for booleans.
 */
fun Boolean.whenFalse(block: Boolean.() -> Unit) {
    if (!this) block(false)
}

/**
 * Invoke function body when the value type is dedicated T.
 *
 * This function helps to reduce the code complexity and increase the development speed by removing the
 * boilerplate if condition for check type checking and casting.
 */
inline fun <reified T> Any.whenTypeIs(block: T.() -> Unit) {
    if (this is T) {
        block(this as T)
    }
}
