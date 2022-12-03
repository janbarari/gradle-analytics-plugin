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
package io.github.janbarari.gradle.analytics.scanner.initialization

import io.github.janbarari.gradle.ExcludeJacocoGenerated
import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.GradleInternal
import org.gradle.api.invocation.Gradle
import org.gradle.internal.InternalBuildListener
import org.gradle.internal.scan.time.BuildScanBuildStartedTime

/**
 * Track and holds the build start and initialization finish timestamp to use by
 * [io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionService].
 */
class BuildInitializationService(
    private val project: Project
) : InternalBuildListener, ProjectEvaluationListener {

    companion object {
        var STARTED_AT: Long = 0L
        var INITIALIZED_AT: Long = 0L

        fun reset() {
            STARTED_AT = 0L
            INITIALIZED_AT = 0L
        }

        fun assignInitializedAt() {
            if(INITIALIZED_AT == 0L) {
                INITIALIZED_AT = System.currentTimeMillis()
            }
        }
    }

    init {
        reset()
    }

    override fun beforeEvaluate(project: Project) {
        assignInitializationTimestamp()
    }

    override fun afterEvaluate(project: Project, state: ProjectState) {
        assignInitializationTimestamp()
    }

    private fun assignInitializationTimestamp() {
        assignInitializedAt()
    }

    @ExcludeJacocoGenerated
    override fun settingsEvaluated(settings: Settings) {
        // Added because gradle allows when InternalBuildListener.kt is implemented in the service class.
    }

    @ExcludeJacocoGenerated
    override fun projectsLoaded(gradle: Gradle) {
        // Added because gradle allows when InternalBuildListener.kt is implemented in the service class.
    }

    @ExcludeJacocoGenerated
    override fun projectsEvaluated(gradle: Gradle) {
        STARTED_AT = getStartTimestamp()
    }

    @ExcludeJacocoGenerated
    @Deprecated("Deprecated")
    override fun buildFinished(result: BuildResult) {
        // Added because gradle allows when InternalBuildListener.kt is implemented in the service class.
    }

    /**
     * Returns the build start timestamp from [org.gradle.internal.scan.time.BuildScanBuildStartedTime].
     */
    @ExcludeJacocoGenerated
    private fun getStartTimestamp(): Long {
        val buildStartedTimeService = (project.gradle as GradleInternal).services.get(BuildScanBuildStartedTime::class.java)
        return buildStartedTimeService?.buildStartedTime ?: System.currentTimeMillis()
    }

}
