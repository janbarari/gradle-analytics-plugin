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
package io.github.janbarari.gradle.analytics.core.print

import io.github.janbarari.gradle.analytics.core.buildscanner.model.BuildInfo

/**
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
object ConsolePrinter {

    private const val SECONDS_PRINT_THRESHOLD = 3000L

    fun printBuildInfo(info: BuildInfo) {
        println()
        println("BUILD INFORMATION")

        if (info.getInitializationDuration().isZero.not()) {
            if (info.getInitializationDuration().toMillis() > SECONDS_PRINT_THRESHOLD) {
                println("INITIALIZED IN %ss".format(info.getInitializationDuration().toSeconds()))
            } else {
                println("INITIALIZED IN %sms".format(info.getInitializationDuration().toMillis()))
            }
        }

        if (info.getConfigurationDuration().isZero.not()) {
            if (info.getConfigurationDuration().toMillis() > SECONDS_PRINT_THRESHOLD) {
                println("CONFIGURED IN %ss".format(info.getConfigurationDuration().toSeconds()))
            } else {
                println("CONFIGURED IN %sms".format(info.getConfigurationDuration().toMillis()))
            }
        }

        if (info.getTotalDependenciesResolveDuration().isZero.not()) {
            if (info.getTotalDependenciesResolveDuration().toMillis() > SECONDS_PRINT_THRESHOLD) {
                println("DEPENDENCIES RESOLVED IN %ss".format(info.getTotalDependenciesResolveDuration().toSeconds()))
            } else {
                println("DEPENDENCIES RESOLVED IN %sms".format(info.getTotalDependenciesResolveDuration().toMillis()))
            }
        }

        if (info.getExecutionDuration().isZero.not()) {
            if (info.getExecutionDuration().toMillis() > SECONDS_PRINT_THRESHOLD) {
                println("EXECUTED IN %ss".format(info.getExecutionDuration().toSeconds()))
            } else {
                println("EXECUTED IN %sms".format(info.getExecutionDuration().toMillis()))
            }
        }

        if (info.getTotalDuration().isZero.not()) {
            if (info.getTotalDuration().toMillis() > SECONDS_PRINT_THRESHOLD) {
                println("BUILD FINISHED IN %ss".format(info.getTotalDuration().toSeconds()))
            } else {
                println("BUILD FINISHED IN %sms".format(info.getTotalDuration().toMillis()))
            }
        }

        println()
    }

}
