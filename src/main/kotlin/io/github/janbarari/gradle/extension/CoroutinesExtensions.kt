package io.github.janbarari.gradle.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

inline fun launchIO(crossinline block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        block(this)
    }
}

inline fun launchDefault(crossinline block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Default).launch {
        block(this)
    }
}

