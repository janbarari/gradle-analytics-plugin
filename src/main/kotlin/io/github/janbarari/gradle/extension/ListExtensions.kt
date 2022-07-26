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
 * Iterates across the list items, but this function allows each iteration to
 * add and remove items from the list.
 */
fun <T: Any> Collection<T>.whenEach(block: T.() -> Unit) {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        block(iterator.next())
    }
}

/**
 * Maps the Long list to Int list.
 */
fun List<Long>.toIntList(): List<Int> {
    return this.map { it.toInt() }
}

/**
 * Checks is the given list has more items than dedicated count.
 */
fun <T> List<T>.isBiggerThan(count: Int): Boolean {
    return this.size > count
}

/**
 * Executes the function body if the given list has no items.
 */
inline fun <T> List<T>.whenEmpty(block: Collection<T>.() -> Unit): List<T> {
    if (isEmpty()) block(this)
    return this
}

/**
 * Represents the first index value.
 */
val <T> List<T>.firstIndex: Int
    get() = 0

/**
 * Checks is the given list has only a single item.
 */
fun <T> List<T>.hasSingleItem(): Boolean {
    return this.size == 1
}

/**
 * Checks is the given list has multiple items.
 */
fun <T> List<T>.hasMultipleItems(): Boolean {
    return this.size > 1
}

fun List<String>.toArrayString(): String {
    val labels = StringBuilder()
    labels.append("[")
    whenEach {
        labels.append("\"$this\"")
            .append(",")
    }
    // because the last item should not have ',' separator.
    labels.removeLastChar()
    labels.append("]")
    return labels.toString()
}
