package io.github.janbarari.gradle.extension

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Rounds the given float number.
 *
 * @param decimalsCount The fraction numbers count, Default is Two.
 */
fun Float.round(decimalsCount: Int = 2): Float {
    val bd = BigDecimal(this.toDouble())
    return bd.setScale(decimalsCount, RoundingMode.FLOOR).toFloat()
}
