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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.janbarari.gradle.analytics.data

import io.github.janbarari.gradle.analytics.domain.model.metric.BuildMetric
import io.github.janbarari.gradle.extension.isNotNull
import io.github.janbarari.gradle.logger.Tower
import io.github.janbarari.gradle.memorycache.MemoryCache

class TemporaryMetricsMemoryCacheImpl(
    private val tower: Tower
) : MemoryCache<List<BuildMetric>> {

    companion object {
        private val clazz = TemporaryMetricsMemoryCacheImpl::class.java
    }

    private var value: List<BuildMetric>? = null
    private var timestamp = 0L

    override fun write(value: List<BuildMetric>) {
        tower.i(clazz, "write()")
        this.value = value
        this.timestamp = System.currentTimeMillis()
    }

    override fun read(): List<BuildMetric>? {
        tower.i(clazz, "read()")
        return value
    }

    override fun isValid(): Boolean {
        tower.i(clazz, "isValid()")
        return value.isNotNull() && System.currentTimeMillis() - timestamp < 60_000
    }

    override fun invalidate() {
        tower.i(clazz, "invalidate()")
        value = null
        timestamp = 0L
    }
}
