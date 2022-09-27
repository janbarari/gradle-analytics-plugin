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
 * Map the Long list to Int list.
 */
fun List<Long>.toIntList(): List<Int> {
    return this.map { it.toInt() }
}

/**
 * Check is the given list has more items than dedicated count.
 */
fun <T> List<T>.isBiggerThan(count: Int): Boolean {
    return this.size > count
}

/**
 * Invoke the function body if the given list has no items.
 */
inline fun <T> List<T>.whenEmpty(block: Collection<T>.() -> Unit): List<T> {
    if (isEmpty()) block(this)
    return this
}

/**
 * Invoke the function body if the given list is not empty.
 */
inline fun <T> List<T>.whenNotEmpty(block: Collection<T>.() -> Unit): List<T> {
    if (isNotEmpty()) block(this)
    return this
}

/**
 * Invoke the function body if the given set is not empty.
 */
inline fun <T> Set<T>.whenNotEmpty(block: Collection<T>.() -> Unit): Set<T> {
    if (isNotEmpty()) block(this)
    return this
}

/**
 * Get the first index value.
 */
val <T> List<T>.firstIndex: Int
    get() = 0

/**
 * Check is the given list has only a single item.
 */
fun <T> List<T>.hasSingleItem(): Boolean {
    return this.size == 1
}

/**
 * Check is the given list has multiple items.
 */
fun <T> List<T>.hasMultipleItems(): Boolean {
    return this.size > 1
}

/**
 * Convert list of string to comma separated string.
 */
fun List<String>.toArrayString(): String {
    val labels = StringBuilder()
    labels.append("[")
    whenEach {
        labels.append("\"$this\"")
            .append(",")
    }
    if (labels.length > 1) {
        // because the last item should not have ',' separator.
        labels.removeLastChar()
    }
    labels.append("]")
    return labels.toString()
}

/**
 * I was refactoring my Gradle plugin source code and I saw that I use
 * "map{}" and "list duplication" to create the same list with some modifications,
 * I find out this is a bug because it:
 *
 * 1- leaks the performance by "object recreation" and "memory duplication".
 * 2- Decreases the readability by putting the modification operation outside of collection operators.
 * 3- Decreases the extensibility because it is not a collection operator, so can't use a chain with other collection operators.
 *
 * Then I decided to create a modification operator for a list to manipulate items in place.
 * Less code
 * More extensibility
 * Better performance
 */
public inline fun <T> Iterable<T>.modify(modification: T.() -> Unit): Iterable<T> {
    for (item in this)
        item.apply(modification)
    return this
}

public inline fun <T> Collection<T>.modify(modification: T.() -> Unit): Collection<T> {
    for (item in this)
        item.apply(modification)
    return this
}

public inline fun <T> List<T>.modify(modification: T.() -> Unit): List<T> {
    for (item in this)
        item.apply(modification)
    return this
}
