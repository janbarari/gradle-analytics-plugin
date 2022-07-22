package io.github.janbarari.gradle.extension

fun Int.diffPercentageOf(target: Int): Float {
    val result = ((target.toFloat() - this.toFloat()) / this.toFloat()) * 100F
    return result.round()
}

fun Long.diffPercentageOf(target: Long): Float {
    val result = ((target.toFloat() - this.toFloat()) / this.toFloat()) * 100F
    return result.round()
}

fun Int.toPercentageOf(target: Int): Float {
    return ((this.toFloat() * 100F) / target).round()
}
