/*
 * Copyright (c) 2018, Omnisol Ltd. All rights reserved.
 * OMNISOL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package zakhi.advent

import zakhi.advent.helpers.withResourceStream

fun main() {
    val claims = readClaims()
    val squareCoverCounts = claims.asSequence().flatMap { it.squaresCovered() }.groupingBy { it }.eachCount()

    println("Part 1: ${squareCoverCounts.count { it.value > 1 }}")
    println("Part 2: ${findNonOverlapping(claims, squareCoverCounts)}")
}


data class Claim(val id: Int, val left: Int, val top: Int, val width: Int, val height: Int) {

    private val right get() = left + width
    private val bottom get() = top + height

    fun squaresCovered(): Sequence<Pair<Int, Int>> = sequence {
        for (x in left until right) {
            for (y in top until bottom) {
                yield(x to y)
            }
        }
    }
}


private fun readClaims(): List<Claim> = withResourceStream("day 3 input.txt") {
    it.bufferedReader().readLines().map { line ->
        val match = Regex("""#(\d+) @ (\d+),(\d+): (\d+)x(\d+)""").matchEntire(line) ?: throw IllegalStateException("invalid input")
        val (id, left, top, width, height) = match.groupValues.drop(1).map(String::toInt)
        Claim(id, left, top, width, height)
    }
}


fun findNonOverlapping(claims: List<Claim>, squareCoverCounts: Map<Pair<Int, Int>, Int>): String {
    val nonOverlappingClaim = claims.find { claim ->
        claim.squaresCovered().all { coordinates -> squareCoverCounts[coordinates] == 1 }
    }

    return nonOverlappingClaim?.id.toString()
}
