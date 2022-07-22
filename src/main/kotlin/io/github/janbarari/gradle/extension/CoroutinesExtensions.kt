package io.github.janbarari.gradle.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Launches an IO coroutine and returns the job.
 */
inline fun launchIO(crossinline block: suspend CoroutineScope.() -> Unit): Job {
    return CoroutineScope(Dispatchers.IO).launch {
            block(this)
        }
}

/**
 * Launches a Default coroutine and returns the job.
 */
inline fun launchDefault(crossinline block: suspend CoroutineScope.() -> Unit): Job {
    return CoroutineScope(Dispatchers.Default)
        .launch {
            block(this)
        }
}
