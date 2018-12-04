/*
 * Copyright (c) 2018, Omnisol Ltd. All rights reserved.
 * OMNISOL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package zakhi.advent

import zakhi.advent.helpers.withResourceStream

fun main() {
    val boxIds = readBoxIds()
    val (twice, threeTimes) = countRepeatedLetters(boxIds)

    println("Part 1: ${twice * threeTimes}")
    println("Part 2: ${findMatchingIds(boxIds)}")
}

private fun readBoxIds(): List<String> = withResourceStream("day 2 input.txt") {
    it.bufferedReader().readLines()
}

private fun countRepeatedLetters(ids: List<String>): Pair<Int, Int> {
    var twice = 0
    var threeTimes = 0

    ids.map { id -> id.groupingBy { it }.eachCount() }.forEach { letterFrequencies ->
        if (letterFrequencies.any { it.value == 2 }) twice++
        if (letterFrequencies.any { it.value == 3 }) threeTimes++
    }

    return twice to threeTimes
}

private fun findMatchingIds(ids: List<String>): String {
    for ((first, second) in ids.pairs()) {
        val commonLetters = first.zip(second).filter { it.first == it.second }.map { it.first }

        if (first.length - commonLetters.size == 1) {
            return commonLetters.joinToString(separator = "")
        }
    }

    return "No match!"
}


private fun <E> List<E>.pairs(): Sequence<Pair<E, E>> = sequence {
    forEachIndexed { index, element ->
        asSequence().drop(index + 1).forEach { otherElement ->
            yield(element to otherElement)
        }
    }
}
