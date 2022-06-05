package io.github.janbarari.gradle.extension

fun Boolean.whenTrue(block: Boolean.() -> Unit) {
    if (this) block(this)
}

fun Boolean.whenFalse(block: Boolean.() -> Unit) {
    if (!this) block(this)
}
