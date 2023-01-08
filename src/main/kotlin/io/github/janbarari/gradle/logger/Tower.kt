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
package io.github.janbarari.gradle.logger

/**
 * Internal logger for error reporting
 */
interface Tower {

    /**
     * Error log
     * @param clazz Executive class
     * @param message Log message
     */
    fun <T> e(clazz: Class<T>, message: String)

    /**
     * Error log
     * @param clazz Executive class
     * @param method Method name
     * @param message Log message
     */
    fun <T> e(clazz: Class<T>, method: String, message: String)

    /**
     * Warning log
     * @param clazz Executive class
     * @param message Log message
     */
    fun <T> w(clazz: Class<T>, message: String)

    /**
     * Warning log
     * @param clazz Executive class
     * @param method Method name
     * @param message Log message
     */
    fun <T> w(clazz: Class<T>, method: String, message: String)

    /**
     * Info log
     * @param clazz Executive class
     * @param message Log message
     */
    fun <T> i(clazz: Class<T>, message: String)

    /**
     * Info log
     * @param clazz Executive class
     * @param method Method name
     * @param message Log message
     */
    fun <T> i(clazz: Class<T>, method: String, message: String)

    /**
     * Raw log
     * @param message log message
     */
    fun r(message: String)

    /**
     * Generate log file
     */
    fun save()
}
