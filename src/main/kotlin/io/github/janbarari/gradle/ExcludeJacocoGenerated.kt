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
package io.github.janbarari.gradle

/**
 * To ensure reliable code coverage, it may be necessary to exclude certain functions
 * or classes from analysis, especially those whose lifecycle is based on the framework
 * and not controlled by the developer.
 *
 * Fortunately, as of Jacoco 0.8.2, developers can now exclude classes and methods by
 * annotating them with a custom annotation that meets the following criteria:
 *
 * 1. The name of the annotation should include the keyword 'Generated'.
 * 2. The retention policy of the annotation should be set to 'runtime' or 'class'.
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION,
    AnnotationTarget.CLASS, AnnotationTarget.FIELD)
annotation class ExcludeJacocoGenerated
