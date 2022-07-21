package io.github.janbarari.gradle.extension

@Suppress("MagicNumber")
fun Int.diffPercentageOf(target: Int): Float {
    val result = ((target.toFloat() - this.toFloat()) / this.toFloat()) * 100F
    return result.round()
}

@Suppress("MagicNumber")
fun Long.diffPercentageOf(target: Long): Float {
    val result = ((target.toFloat() - this.toFloat()) / this.toFloat()) * 100F
    return result.round()
}

@Suppress("MagicNumber")
fun Int.toPercentageOf(target: Int): Float {
    return ((this.toFloat() * 100F) / target).round()
}
