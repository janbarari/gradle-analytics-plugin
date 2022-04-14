package io.github.janbarari.gradle.analytics.core

import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.internal.operations.OperationStartEvent
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.OperationDescriptor
import org.gradle.tooling.events.task.TaskFinishEvent
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue

abstract class BuildTimeRecorder : BuildService<BuildTimeRecorder.Params>, OperationCompletionListener, AutoCloseable {
    interface Params : BuildServiceParameters {
        fun getParams(): Property<String>
    }

    private val records: MutableCollection<TaskInfo> = ConcurrentLinkedQueue()
    private val buildStartTime: Instant = Instant.now()

    override fun onFinish(event: FinishEvent?) {
        if (event is TaskFinishEvent) {
           records.add(
               TaskInfo(
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
        val buildEndTime = Instant.now()
        val buildInfo = BuildInfo(
            records,
            buildStartTime.toEpochMilli(),
            buildEndTime.toEpochMilli()
        )
        BuildGlobalListener.buildFinished(buildInfo)
    }
}
