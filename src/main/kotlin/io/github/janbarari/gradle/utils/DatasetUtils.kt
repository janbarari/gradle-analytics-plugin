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

/**
 * A collection of dataset functions.
 *
 * Note: Most of the algorithms have been customized based on the plugin's requirements.
 */
object DatasetUtils {

    /**
     * Resizes a dataset to another dataset with the target size.
     *
     * Note: It uses Mean for resizing datasets and guarantees no data is skipped. According
     * to the algorithm, the result dataset size can be smaller than target if the enough
     * data do not exist on the input dataset.
     */
    fun resizeDataset(input: List<Long>, targetSize: Int): List<Long> {
        return if (input.size > targetSize)
            resizeDataset(meanDataset(input), targetSize)
        else
            input
    }

    /**
     * Creates a Mean dataset from input dataset.
     */
    fun meanDataset(input: List<Long>): List<Long> {
        if (input.isEmpty()) return input
        val mean = arrayListOf<Long>()
        var nextIndex = 0
        val size = input.size
        for (i in input.indices) {
            if (i >= nextIndex) {
                if(i + 1 >= size) {
                    mean.add(input[i])
                } else {
                    mean.add(
                        (input[i] + input[i + 1]) / 2
                    )
                    nextIndex = i + 2
                }
            }
        }
        return mean
    }

}
