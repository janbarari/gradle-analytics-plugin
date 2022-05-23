package io.github.janbarari.gradle.utils

import java.io.BufferedReader
import java.io.InputStreamReader

fun execCommand(cmd: String): String {
    val runtime = Runtime.getRuntime()
    try {
        val reader = BufferedReader(
            InputStreamReader(runtime.exec(cmd).inputStream)
        )
        val result = reader.readLine()
        result ?: "undefined"
        return result
    } catch (e: IllegalStateException) {
        throw IllegalArgumentException("Error executing $cmd.", e)
    }
}
