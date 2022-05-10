/**
 * MIT License
 * Copyright (c) 2022 Mehdi Janbarari (@janbarari)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.bus

import java.io.Serializable
import kotlin.collections.HashMap

/**
 * @author Mehdi-Janbarari
 * @since 1.0.0
 */
class DefaultEvent : Serializable {

    private var sender: Class<*>
    private var data = HashMap<String, Any>()

    constructor(sender: Class<*>) {
        this.sender = sender
    }

    private constructor(sender: Class<*>, data: HashMap<String, Any>) {
        this.sender = sender
        this.data = data
    }

    /**
     * Represents the event sender class.
     */
    fun getSender(): Class<*> {
        return sender
    }

    /**
     * Checks the key-value exists in the event or not.
     */
    fun containsKey(key: String): Boolean {
        return data.containsKey(key)
    }

    /**
     * Adds a key-value in the event body.
     */
    fun put(key: String, value: Any): DefaultEvent {
        data[key] = value
        return DefaultEvent(sender, data)
    }

    /**
     * Returns the key-value
     */
    operator fun get(key: String): Any {
        return data[key]!!
    }

    override fun toString(): String {
        return "DefaultEvent($sender, $data)"
    }
}
