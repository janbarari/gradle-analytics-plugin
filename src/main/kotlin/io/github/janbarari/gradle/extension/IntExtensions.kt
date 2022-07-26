package io.github.janbarari.gradle.extension

/**
 * Returns the current value difference as percentage from the target value.
 *
 * Example:
 *  val a = 10
 *  val b = 100
 *  a.diffPercentageOf(b) equals 100%+ (b is 100%+ of a)
 */
fun Int.diffPercentageOf(target: Int): Float {
    val result = ((target.toFloat() - this.toFloat()) / this.toFloat()) * 100F
    return result.round()
}

/**
 * Returns the current value difference as percentage from the target value.
 *
 * Example:
 *  val a = 10
 *  val b = 100
 *  a.diffPercentageOf(b) equals 100%+ (b is 100%+ of a)
 */
fun Long.diffPercentageOf(target: Long): Float {
    val result = ((target.toFloat() - this.toFloat()) / this.toFloat()) * 100F
    return result.round()
}

/**
 * Returns the current value coverage from the target value.
 *
 * Example:
 *  val a = 10
 *  val b = 100
 *  a.toPercentageOf(b) equals 10% (a is 10% of b).
 */
fun Int.toPercentageOf(target: Int): Float {
    return ((this.toFloat() * 100F) / target).round()
}
