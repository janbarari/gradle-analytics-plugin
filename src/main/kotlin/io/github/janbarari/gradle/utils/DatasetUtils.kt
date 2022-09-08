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
package io.github.janbarari.gradle.utils

import io.github.janbarari.gradle.analytics.domain.model.TimespanPoint
import io.github.janbarari.gradle.extension.isBiggerThan
import io.github.janbarari.gradle.extension.isNull

/**
 * A collection of data processing, math and algorithm functions
 */
object DatasetUtils {

    /**
     * Calculates the given time span chart point dataset mean.
     */
    fun getTimespanChartPointMean(values: List<TimespanPoint>): List<TimespanPoint> {
        if (values.isEmpty()) return values

        val result = arrayListOf<TimespanPoint>()
        val datasetSize = values.size
        var nextIndex = 0

        for (index in values.indices) {
            if (index < nextIndex) continue //skip the iteration for ignore duplicate data.

            if (index + 1 >= datasetSize) { //last list item is single. So need to add without calculating the mean.
                result.add(values[index])
            } else {
                result.add(
                    TimespanPoint(
                        value = MathUtils.longMean(values[index].value, values[index + 1].value),
                        from = values[index].from,
                        to = if (values[index + 1].to.isNull()) values[index + 1].from else values[index + 1].to
                    )
                )
                //because the index & index + 1 mean calculated. iteration should be skipped for next index.
                nextIndex = index + 2
            }
        }

        return result
    }

    /**
     * Minimizes the given timespan chart point dataset.
     */
    fun minimizeTimespanChartPoints(dataset: List<TimespanPoint>, maximumSize: Int): List<TimespanPoint> {
        return if (dataset.isBiggerThan(maximumSize))
            minimizeTimespanChartPoints(getTimespanChartPointMean(dataset), maximumSize)
        else dataset
    }

}
