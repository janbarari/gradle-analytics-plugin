package io.github.janbarari.gradle.analytics.core.task

import io.github.janbarari.gradle.bus.Bus
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.task.TaskFinishEvent
import java.util.concurrent.ConcurrentLinkedQueue

abstract class TasksOperation :
    BuildService<TasksOperation.Params>, OperationCompletionListener, AutoCloseable {
    interface Params : BuildServiceParameters {
        fun getParams(): Property<TasksOperationParams>
    }

    private val executedTaskReports: MutableCollection<TaskReport> = ConcurrentLinkedQueue()

    override fun onFinish(event: FinishEvent?) {
        if (event is TaskFinishEvent) {
            executedTaskReports.add(
                TaskReport(
                    event.result.startTime,
                    event.result.endTime,
                    event.descriptor.taskPath,
                    event.descriptor.displayName,
                    event.descriptor.name
                )
            )
        }
    }

    override fun close() {
        val eventGUID = parameters.getParams().get().eventGUID
        Bus.post(executedTaskReports as Collection<TaskReport>, eventGUID)
    }

}
