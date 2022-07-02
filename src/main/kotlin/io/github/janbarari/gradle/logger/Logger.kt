package io.github.janbarari.gradle.logger

import io.github.janbarari.gradle.extension.launchDefault
import io.github.janbarari.gradle.utils.DateTimeUtils

class Logger {
    companion object {
        var isSilent = false
    }
}

fun info(tag: String, message: String) {
    if (!Logger.isSilent) {
        launchDefault {
            val timestamp = DateTimeUtils.format(System.currentTimeMillis(), "yyyy/MM/dd HH:mm:ss a 'UTC'")
            println("Info: $timestamp $tag $message")
        }
    }
}

fun alert(tag: String, message: String) {
    if (!Logger.isSilent) {
        launchDefault {
            val timestamp = DateTimeUtils.format(System.currentTimeMillis(), "yyyy/MM/dd HH:mm:ss a 'UTC'")
            println("Alert: $timestamp $tag $message")
        }
    }
}

fun error(tag: String, message: String, throwable: Throwable? = null) {
    if (!Logger.isSilent) {
        launchDefault {
            val timestamp = DateTimeUtils.format(System.currentTimeMillis(), "yyyy/MM/dd HH:mm:ss a 'UTC'")
            println("Error: $timestamp $tag $message")
            throwable?.printStackTrace()
        }
    }
}
