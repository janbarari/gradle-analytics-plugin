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
package io.github.janbarari.gradle.analytics.plugin.buildscanner

import org.gradle.BuildResult
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.internal.InternalBuildListener

/**
 * Track and holds the build configuration finish timestamp to use by [BuildExecutionService].
 *
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
class BuildConfigurationService : InternalBuildListener {

    companion object {
        var CONFIGURED_AT: Long = 0L

        fun reset() {
            CONFIGURED_AT = 0L
        }

    }

    init {
        reset()
    }

    override fun settingsEvaluated(settings: Settings) {
        // called when the root project settings evaluated.
    }

    override fun projectsLoaded(gradle: Gradle) {
        // called when projects files loaded.
    }

    override fun projectsEvaluated(gradle: Gradle) {
        CONFIGURED_AT = System.currentTimeMillis()
    }

    @Deprecated("Deprecated")
    override fun buildFinished(result: BuildResult) {
        // This method is deprecated, Execution process are handled by [BuildExecutionService]
    }

}
