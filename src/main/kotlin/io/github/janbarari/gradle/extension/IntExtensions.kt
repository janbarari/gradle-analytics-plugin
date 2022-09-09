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
 * Returns the current value difference as percentage from the target value.
 *
 * Example:
 *  val a = 10
 *  val b = 100
 *  a.diffPercentageOf(b) equals 100%+ (b is 100%+ of a)
 */
fun Int.diffPercentageOf(target: Int): Float {
    if(this == 0) return 0F
    val result = ((target.toFloat() - this.toFloat()) / this.toFloat()) * 100F
    return result.round()
}

/**
 * Returns the current value difference as percentage from the target value.
 *
 * Example:
 *  val a = 10
 *  val b = 100
 *  a.diffPercentageOf(b) equals 100%+ (b is 100%+ of a)
 */
fun Long.diffPercentageOf(target: Long): Float {
    if(this == 0L) return 0F
    val result = ((target.toFloat() - this.toFloat()) / this.toFloat()) * 100F
    return result.round()
}

/**
 * Returns the current value coverage from the target value.
 *
 * Example:
 *  val a = 10
 *  val b = 100
 *  a.toPercentageOf(b) equals 10% (a is 10% of b).
 */
fun Int.toPercentageOf(target: Int): Float {
    if (target == 0) return 0f
    return ((this.toFloat() * 100F) / target).round()
}

/**
 * Returns the current value coverage from the target value.
 *
 * Example:
 *  val a = 10
 *  val b = 100
 *  a.toPercentageOf(b) equals 10% (a is 10% of b).
 */
fun Long.toPercentageOf(target: Long): Float {
    if (target == 0L) return 0F
    return ((this.toFloat() * 100F) / target).round()
}

