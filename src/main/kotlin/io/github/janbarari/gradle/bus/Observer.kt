package io.github.janbarari.gradle.bus

import java.util.UUID

class Observer(
    var observerType: Class<*>,
    var guid: String,
    var unit: (Any) -> Unit,
    var sender: Class<*>? = null) {

    companion object {
        fun generateGUID(): String {
            return UUID.randomUUID().toString()
        }
    }

    override fun toString(): String {
        return "Subscriber($guid, ${observerType::class.java.name})"
    }

}