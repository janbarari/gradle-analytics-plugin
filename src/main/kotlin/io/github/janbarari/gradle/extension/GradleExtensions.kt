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
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.invocation.Gradle
import java.util.*

/**
 * Get the Gradle requested tasks list. `requestedTasks` are the tasks that CLI
 * sent them to Gradle to start the build process.
 */
@ExcludeJacocoGenerated
fun Gradle.getRequestedTasks(): List<String> {
    return startParameter.taskNames
}

/**
 * Get the 'CI' value provider from the system environments.
 */
@ExcludeJacocoGenerated
fun envCI(): Boolean {
    if(System.getenv("CI").isNull()) {
        return false
    }
    return System.getenv("CI").toBoolean()
}

/**
 * Check is project dependency has dependency to ProjectDependency type.
 */
@ExcludeJacocoGenerated
fun Project.isModuleProject(): Boolean {
    return plugins.hasPlugin("com.android.application") ||
            plugins.hasPlugin("com.android.library") ||
            plugins.hasPlugin("jvm") ||
            plugins.hasPlugin("org.jetbrains.kotlin.jvm") ||
            plugins.hasPlugin("java-library") ||
            plugins.hasPlugin("org.gradle.java-library") ||
            plugins.hasPlugin("org.gradle.java")

}

/**
 * Register the given task.
 */
@ExcludeJacocoGenerated
inline fun <reified T : DefaultTask> Project.registerTask(name: String, noinline block: T.() -> Unit) {
    project.tasks.register(name, T::class.java, block::invoke)
}

/**
 * Create the given task.
 */
@ExcludeJacocoGenerated
inline fun <reified T : DefaultTask> Project.createTask(name: String): T {
    return project.tasks.create(name, T::class.java)
}

/**
 * Check if the given Task is cacheable.
 */
@ExcludeJacocoGenerated
fun Task.isCacheable(): Boolean {
    return this.outputs.hasOutput && this.inputs.hasInputs
}

/**
 * Get task dependencies.
 */
@ExcludeJacocoGenerated
fun Task.getSafeTaskDependencies(): Set<Task> {
    return try {
        taskDependencies.getDependencies(this)
    } catch (e: Throwable) {
        emptySet()
    }
}

/**
 * Filter non-cacheable tasks from the given task collection.
 */
@ExcludeJacocoGenerated
fun List<Task>.getNonCacheableTasks(): Set<String> {
    return try {
        val nonCacheableTasks = Collections.synchronizedSet(mutableSetOf<String>())
        forEach { task ->
            task.getSafeTaskDependencies()
                .filter { !it.isCacheable() }
                .whenNotEmpty {
                    nonCacheableTasks.addAll(this.map { it.path })
                }
        }
        return nonCacheableTasks
    } catch (e: Throwable) {
        // H-46: return emptySet() when there is unexpected issue.
        emptySet()
    }
}
