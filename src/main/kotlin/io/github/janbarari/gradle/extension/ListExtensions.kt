package io.github.janbarari.gradle.extension

fun <T: Any> List<T>.whenEach(block: T.() -> Unit) {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        block(iterator.next())
    }
}

fun List<Long>.toIntList(): List<Int> {
    return this.map { it.toInt() }
}
