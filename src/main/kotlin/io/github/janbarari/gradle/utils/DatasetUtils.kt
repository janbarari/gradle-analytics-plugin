package io.github.janbarari.gradle.utils

fun resizeDataset(input: List<Long>, target: Int): List<Long> {
    return if (input.size > target)
        resizeDataset(meanDataset(input), target)
    else
        input
}

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
