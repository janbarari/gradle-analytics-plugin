package io.github.janbarari.gradle.extension

fun Long.isZero(): Boolean {
    return this == 0L
}

fun Long.isBiggerEquals(value: Long): Boolean {
    return this >= value
}

fun Long.isBigger(value: Long): Boolean {
    return this > value
}

