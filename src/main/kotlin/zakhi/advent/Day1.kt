/*
 * Copyright (c) 2018, Omnisol Ltd. All rights reserved.
 * OMNISOL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package zakhi.advent

import zakhi.advent.helpers.withResourceStream

fun main() {
    val frequencyChanges = readFrequencyChanges()

    println("Part 1: ${frequencyChanges.sum()}")
    println("Part 2: ${firstRepeatedFrequency(frequencyChanges)}")
}

private fun readFrequencyChanges(): List<Int> = withResourceStream("day 1 input.txt") {
    it.bufferedReader().lineSequence().map(String::toInt).toList()
}


private fun firstRepeatedFrequency(changes: List<Int>): Int {
    var currentFrequency = 0
    val uniqueFrequencies = mutableSetOf<Int>()

    while (true) {
        for (change in changes) {
            currentFrequency += change
            if (uniqueFrequencies.contains(currentFrequency)) return currentFrequency

            uniqueFrequencies.add(currentFrequency)
        }
    }
}
