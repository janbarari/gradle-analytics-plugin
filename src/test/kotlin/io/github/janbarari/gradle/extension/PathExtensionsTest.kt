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
package io.github.janbarari.gradle.extension

import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.test.assertEquals

class PathExtensionsTest {

    @Test
    fun `when Path#isSourcePath() invoked, validate the result`() {
        var sourcePaths: List<Path>
        Files.walk(Path("./")).use { stream ->
            sourcePaths = stream.map { obj: Path -> obj.normalize() }
                .filter { it.isSourcePath() }
                .collect(Collectors.toList())
        }
        assertEquals(true, sourcePaths.any {
            it.extension != "kt" || it.extension != "java"
        })
    }

    @Test
    fun `when Path#readText() invoked, expect to get the file content as string`() {
        File("build/path-extensions-test-template.txt").writeText("Woman Life Freedom")
        val content = Path("build/path-extensions-test-template.txt").readText()
        assertEquals("Woman Life Freedom", content)
    }

}
