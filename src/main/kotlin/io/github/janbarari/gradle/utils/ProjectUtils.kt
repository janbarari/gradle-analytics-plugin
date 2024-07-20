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
 * The aboVe copyright notice and this permission notice shall be included in all
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

import org.gradle.util.GradleVersion

/**
 * A collection of gradle project functions.
 */
object ProjectUtils {

    /**
     * List of official Gradle releases.
     */
    enum class GradleVersions(val versionNumber: String) {
        V8_9("8.9"),
        V8_8("8.8"),
        V8_7("8.7"),
        V8_6("8.6"),
        V8_5("8.5"),
        V8_4("8.4"),
        V8_3("8.3"),
        V8_2_1("8.2.1"),
        V8_2("8.2"),
        V8_1_1("8.1.1"),
        V8_1("8.1"),
        V8_0_2("8.0.2"),
        V8_0_1("8.0.1"),
        V8_0("8.0"),
        V7_6_1("7.6.1"),
        V7_6("7.6"),
        V7_5_1("7.5.1"),
        V7_5("7.5"),
        V7_4_2("7.4.2"),
        V7_4_1("7.4.1"),
        V7_4("7.4"),
        V7_3_3("7.3.3"),
        V7_3_2("7.3.2"),
        V7_3_1("7.3.1"),
        V7_3("7.3"),
        V7_2("7.2"),
        V7_1_1("7.1.1"),
        V7_1("7.1"),
        V7_0_2("7.0.2"),
        V7_0_1("7.0.1"),
        V7_0("7.0"),
        V6_9_2("6.9.2"),
        V6_9_1("6.9.1"),
        V6_9("6.9"),
        V6_8_3("6.8.3"),
        V6_8_2("6.8.2"),
        V6_8_1("6.8.1"),
        V6_8("6.8"),
        V6_7_1("6.7.1"),
        V6_7("6.7"),
        V6_6_1("6.6.1"),
        V6_6("6.6"),
        V6_5_1("6.5.1"),
        V6_5("6.5"),
        V6_4_1("6.4.1"),
        V6_4("6.4"),
        V6_3("6.3"),
        V6_2_2("6.2.2"),
        V6_2_1("6.2.1"),
        V6_2("6.2"),
        V6_1_1("6.1.1"),
        V6_1("6.1")
    }

    /**
     * Checks that the project's Gradle version is compatible with the entered Gradle version.
     */
    fun isProjectCompatibleWith(version: GradleVersions): Boolean {
        val projectGradleVersion = GradleVersion.current()
        val minimumRequiredGradleVersion = GradleVersion.version(version.versionNumber)
        return projectGradleVersion > minimumRequiredGradleVersion
    }
}
