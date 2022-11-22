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

import io.github.janbarari.gradle.ExcludeJacocoGenerated
import java.io.InputStream
import java.net.URL

/**
 * Due to https://bugs.openjdk.java.net/browse/JDK-6947916 and https://bugs.openjdk.java.net/browse/JDK-8155607,
 * it is necessary to disallow caches to maintain stability on JDK 8 and 11 (and possibly more).
 * Otherwise, simultaneous invocations of Detekt in the same VM can fail spuriously. A similar bug is referenced
 * in https://github.com/detekt/detekt/issues/3396. The performance regression is likely unnoticeable.
 * Due to https://github.com/detekt/detekt/issues/4332 it is included for all JDKs.
 */
fun URL.openSafeStream(): InputStream {
    return openConnection().apply { useCaches = false }.getInputStream()
}

@ExcludeJacocoGenerated
fun <T> Class<T>.getSafeResourceAsStream(name: String): InputStream? {
    return getResource(name)?.openSafeStream()
}

/**
 * Get the given file content as string.
 */
fun Any.getTextResourceContent(fileName: String): String {
    return javaClass.getResource("/$fileName")!!
        .openSafeStream()
        .bufferedReader()
        .use { it.readText() }
}

