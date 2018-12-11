package zakhi.advent

import zakhi.advent.helpers.withResourceStream
import java.util.*
import kotlin.math.max


class Unit(letter: Char) {
    val type = letter.toLowerCase()
    private val polarity = letter.isLowerCase()

    fun reactsWith(other: Unit) = this.type == other.type && this.polarity != other.polarity
}

typealias Polymer = LinkedList<Unit>

fun main() {
    val letters = withResourceStream("day 5 input.txt") { it.bufferedReader().readText().trim() }

    val polymer = Polymer(letters.map(::Unit)).react()
    val unitTypes = polymer.asSequence().distinctBy { it.type }
    val reducedPolymers = unitTypes.map { polymer.withoutUnit(it).react() }

    println("Part 1: ${polymer.size}")
    println("Part 2: ${reducedPolymers.map { it.size }.min()}")
}

private fun Polymer.react(): Polymer {
    var index = 0

    while (index < size - 1) {
        val currentUnit = this[index]
        val nextUnit = this[index + 1]

        if (currentUnit.reactsWith(nextUnit)) {
            repeat(2) { removeAt(index) }
            index = max(index - 1, 0)
        } else {
            index++
        }
    }

    return this
}

private fun Polymer.withoutUnit(unit: Unit) = Polymer(this).apply { removeIf { it.type == unit.type } }
