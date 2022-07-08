package io.github.janbarari.gradle.extension

import java.math.BigDecimal
import java.math.RoundingMode

fun Float.round(decimalsCount: Int = 2): Float {
    val bd = BigDecimal(this.toDouble())
    return bd.setScale(decimalsCount, RoundingMode.FLOOR).toFloat()
}
