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
package io.github.janbarari.gradle.analytics.core.task

import io.github.janbarari.gradle.bus.Bus
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.task.TaskFinishEvent
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * TasksOperationService tracks the task's execution lifecycle and sends
 * the task's lifecycle report via [Bus] event publisher to the registered
 * receiver.
 *
 * [Bus] need [TasksLifecycleParams.receiverGUID] to send the event to the receiver.
 */
abstract class TasksLifecycleService : BuildService<TasksLifecycleService.Params>,
    OperationCompletionListener, AutoCloseable {

    interface Params : BuildServiceParameters {
        fun getParams(): Property<TasksLifecycleParams>
    }

    private val tasksLifecycleReport: MutableCollection<TaskLifecycle> = ConcurrentLinkedQueue()

    /**
     * Invoked when each task finished event received.
     * @param event task finish event
     */
    override fun onFinish(event: FinishEvent?) {
        if (event is TaskFinishEvent) {
            tasksLifecycleReport.add(
                TaskLifecycle(
                    event.result.startTime,
                    event.result.endTime,
                    event.descriptor.taskPath,
                    event.descriptor.displayName,
                    event.descriptor.name
                )
            )
        }
    }

    /**
     * Invoked when the Gradle build process finished.
     */
    override fun close() {
        val eventGUID = parameters.getParams().get().receiverGUID
        Bus.post(tasksLifecycleReport as Collection<TaskLifecycle>, eventGUID)
    }

}
