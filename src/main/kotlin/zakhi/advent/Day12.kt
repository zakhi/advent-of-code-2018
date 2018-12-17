package zakhi.advent

import zakhi.advent.helpers.withResourceStream


fun main() {
    val (initialState, rules) = withResourceStream("day 12 input.txt") {
        val reader = it.bufferedReader()
        val initialState = readInitialState(reader.readLine())
        reader.readLine()

        val rules = readRules(reader.lineSequence())
        initialState to rules
    }

    val finalState = (1..20).fold(initialState) { currentState, _ -> currentState.transform(rules) }
    println("Part 1: ${finalState.totalPlants}")
}

fun readInitialState(line: String): State {
    val match = Regex("""initial state: ([.#]+)""").matchEntire(line) ?: throw Exception("Invalid line $line")
    return State(match.groupValues[1])
}

fun readRules(lines: Sequence<String>): List<Rule> {
    val regex = Regex("""([.#]{5}) => ([.#])""")

    return lines.map { line ->
        val match = regex.matchEntire(line) ?: throw Exception("Invalid line $line")
        Rule(match.groupValues[1], match.groupValues[2].first())
    }.toList()
}

class State(private val plants: String, private val firstIndex: Int = 0) {

    val totalPlants: Int get() = plants.count { it == '#' }

    fun transform(rules: List<Rule>): State {
        return this
    }
}


data class Rule(val pattern: String, val result: Char)