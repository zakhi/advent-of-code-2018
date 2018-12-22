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

    val gen20State = initialState.transformations(rules).take(20).last()
    println("Part 1: ${gen20State.plantSum}")

    val repeatingState = gen20State.transformations(rules).zipWithNext().first { (previous, current) ->
        previous.pattern == current.pattern && previous.offset == current.offset - 1
    }.second

    val finalState = Plants(
        pattern = repeatingState.pattern,
        generation = 50_000_000_000,
        offset = repeatingState.offset + 50_000_000_000 - repeatingState.generation
    )

    println("Part 2: ${finalState.plantSum}")
}

fun readInitialState(line: String): Plants {
    val match = Regex("""initial state: ([.#]+)""").matchEntire(line) ?: throw Exception("Invalid line $line")
    return Plants(generation = 0, pattern = match.groupValues[1])
}

fun readRules(lines: Sequence<String>): Map<String, Char> {
    val regex = Regex("""([.#]{5}) => ([.#])""")

    return lines.associate { line ->
        val match = regex.matchEntire(line) ?: throw Exception("Invalid line $line")
        match.groupValues[1] to match.groupValues[2].first()
    }
}

data class Plants(val generation: Long, val pattern: String, val offset: Long = 0) {

    val plantSum: Long = pattern.mapIndexed { index, c -> if (c == '#') offset + index else 0 }.sum()

    fun transformations(rules: Map<String, Char>): Sequence<Plants> = sequence {
        var currentPlants = this@Plants

        while (true) {
            currentPlants = currentPlants.transform(rules)
            yield(currentPlants)
        }
    }

    private fun transform(rules: Map<String, Char>): Plants {
        val nextGenPattern = neighborPatterns.map { pattern -> rules[pattern] ?: '.' }.joinToString(separator = "")
        val newOffset = nextGenPattern.takeWhile { it == '.' }.count()

        return Plants(generation = generation + 1, pattern = nextGenPattern.trim('.'), offset = offset + newOffset - 2)
    }

    private val neighborPatterns: Sequence<String>
        get() = sequence {
            val extendedPlants = "....$pattern...."

            for (index in 0..(extendedPlants.length - 5)) {
                yield(extendedPlants.substring(index, index + 5))
            }
        }
}
