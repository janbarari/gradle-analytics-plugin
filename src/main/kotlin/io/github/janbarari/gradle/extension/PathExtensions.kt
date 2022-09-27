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

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.pathString

/**
 * Check is given path is Kotlin/Java source file.
 */
fun Path.isSourcePath(): Boolean {
    return (pathString.contains("src/main/java") || pathString.contains("src/main/kotlin"))
            && (extension == "kt" || extension == "java")
            && Files.isRegularFile(this)
}

/**
 * Check is the given path is Kotlin file.
 */
fun Path.isKotlinFile(): Boolean = extension == "kt"

/**
 * Check is the given path is Java file.
 */
fun Path.isJavaFile(): Boolean = extension == "java"

/**
 * Get given path file content as string.
 */
fun Path.readText(): String {
    return toFile()
        .inputStream()
        .bufferedReader()
        .use { it.readText() }
}
