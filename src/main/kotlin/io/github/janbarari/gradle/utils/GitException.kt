package io.github.janbarari.gradle.utils

class GitException(msg: String): Throwable() {
    override val message: String = msg
}
