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

import io.github.janbarari.gradle.extension.isBiggerThan
import io.github.janbarari.gradle.extension.whenEach
import io.github.janbarari.gradle.utils.DateTimeUtils
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

/**
 * Implementation of [io.github.janbarari.gradle.logger.Tower].
 *
 * @param name File name.
 * @param outputPath Path to save the log file.
 * @param shouldDropOldLogFile Remove previous log file if exists.
 * @param maximumOldLogsCount The number of old logs that keeps in the new log file.
 */
class TowerImpl constructor(
    private val name: String,
    private val outputPath: Path,
    private val shouldDropOldLogFile: Boolean,
    private val maximumOldLogsCount: Int
): Tower {

    private val logFile = getLogPath().toFile()
    private var logs: MutableList<String> = Collections.synchronizedList(mutableListOf())

    init {
        if (shouldDropOldLogFile) {
            dropPreviousLogFile()
        }
    }

    override fun <T> i(clazz: Class<T>, method: String, message: String) {
        logs.add("i: ${getFormattedTime()}, ${clazz.simpleName}.$method, $message")
    }

    override fun <T> i(clazz: Class<T>, message: String) {
        logs.add("i: ${getFormattedTime()}, ${clazz.simpleName}, $message")
    }

    override fun <T> e(clazz: Class<T>, method: String, message: String) {
        logs.add("e: ${getFormattedTime()}, ${clazz.simpleName}.$method, $message")
    }

    override fun <T> e(clazz: Class<T>, message: String) {
        logs.add("e: ${getFormattedTime()}, ${clazz.simpleName}, $message")
    }

    override fun <T> w(clazz: Class<T>, method: String, message: String) {
        logs.add("w: ${getFormattedTime()}, ${clazz.simpleName}.$method, $message")
    }

    override fun <T> w(clazz: Class<T>, message: String) {
        logs.add("w: ${getFormattedTime()}, ${clazz.simpleName}, $message")
    }

    override fun r(message: String) {
        logs.add("r: ${getFormattedTime()}, $message")
    }

    override fun save() {
        if (logFile.exists()) {
            dropOldLogs()
        }

        buildString {
            logs.whenEach {
                append(this).append("\n")
            }
        }.also {
            if (!shouldDropOldLogFile) {
                val separator = "\n\n\n\n"
                logFile.appendText(it + separator)
            } else {
                logFile.appendText(it)
            }
        }

        logs.clear()
    }

    private fun getLogPath(): Path {
        outputPath.toAbsolutePath().toFile().mkdirs()
        return Path("${outputPath.toAbsolutePath()}/${name}-log.txt")
    }

    private fun dropOldLogs() {
        if (logFile.readLines().isBiggerThan(maximumOldLogsCount)) {
            val oldLogs = logFile.readLines().takeLast(maximumOldLogsCount)
            if(!logFile.delete()) {
                // Delete log file failed
            }

            buildString {
                oldLogs.whenEach {
                    append(this).append("\n")
                }
            }.also {
                logFile.appendText(it)
            }
        }
    }

    private fun dropPreviousLogFile(): Boolean {
        if (logFile.exists()) {
            return logFile.delete()
        }
        return false
    }

    private fun getFormattedTime(): String {
        return DateTimeUtils.formatToFullDateTime(System.currentTimeMillis())
    }
}
