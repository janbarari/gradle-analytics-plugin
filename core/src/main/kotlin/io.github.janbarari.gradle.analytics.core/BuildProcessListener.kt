package io.github.janbarari.gradle.analytics.core

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.execution.TaskExecutionGraphListener
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

class BuildProcessListener: BuildListener, TaskExecutionGraphListener {
    override fun settingsEvaluated(settings: Settings) {
        TODO("Not yet implemented")
    }

    override fun projectsLoaded(gradle: Gradle) {
        TODO("Not yet implemented")
    }

    override fun projectsEvaluated(gradle: Gradle) {
        TODO("Not yet implemented")
    }

    override fun buildFinished(result: BuildResult) {
        TODO("Not yet implemented")
    }

    override fun graphPopulated(graph: TaskExecutionGraph) {
        graph.addTaskExecutionListener(object:TaskExecutionListener {
            override fun beforeExecute(task: Task) {

            }
            override fun afterExecute(task: Task, state: TaskState) {

            }
        })
    }

}