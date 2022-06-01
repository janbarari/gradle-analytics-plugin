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
 * Returns True, If the object is null.
 */
fun Any?.isNull(): Boolean {
    return this == null
}

/**
 * Returns True, If the object is not null.
 */
fun Any?.isNotNull(): Boolean {
    return this != null
}

/**
 * Invokes the lambda function if the object is NOT null.
 */
fun <T: Any> T?.whenNotNull(block: T.() -> Unit) {
    if (this != null) block(this)
}

/**
 * Invokes the lambda function if the object is null.
 */
fun <T: Any> T?.whenNull(block: () -> Unit) {
    if(this == null) block()
}

inline fun <reified T: Any> ensureNotNull(value: T?): T {
    if (value.isNull()) throw java.lang.NullPointerException("${T::class} can not be null")
    return value as T
}