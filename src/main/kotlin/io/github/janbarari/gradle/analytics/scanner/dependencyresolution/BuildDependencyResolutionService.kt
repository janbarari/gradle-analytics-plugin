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
package io.github.janbarari.gradle.analytics.scanner.dependencyresolution

import io.github.janbarari.gradle.analytics.domain.model.DependencyResolveInfo
import io.github.janbarari.gradle.ExcludeJacocoGenerated
import org.gradle.BuildResult
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.internal.InternalBuildListener
import java.util.concurrent.ConcurrentHashMap

/**
 * Records the build dependencies resolve information to use by
 * [io.github.janbarari.gradle.analytics.scanner.execution.BuildExecutionService].
 */
class BuildDependencyResolutionService : InternalBuildListener, DependencyResolutionListener {

    companion object {
        val dependenciesResolveInfo: ConcurrentHashMap<String, DependencyResolveInfo> =
            ConcurrentHashMap<String, DependencyResolveInfo>()

        fun reset() {
            dependenciesResolveInfo.clear()
        }

    }

    init {
        reset()
    }

    @ExcludeJacocoGenerated
    override fun beforeResolve(dependencies: ResolvableDependencies) {
        dependenciesResolveInfo[dependencies.path] = DependencyResolveInfo(
            dependencies.path,
            startedAt = System.currentTimeMillis()
        )
    }

    @ExcludeJacocoGenerated
    override fun afterResolve(dependencies: ResolvableDependencies) {
        dependenciesResolveInfo[dependencies.path]?.finishedAt = System.currentTimeMillis()
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
        // Added because gradle allows when InternalBuildListener.kt is implemented in the service class.
    }

    @ExcludeJacocoGenerated
    @Deprecated("Deprecated")
    override fun buildFinished(result: BuildResult) {
        // Added because gradle allows when InternalBuildListener.kt is implemented in the service class.
    }

}
