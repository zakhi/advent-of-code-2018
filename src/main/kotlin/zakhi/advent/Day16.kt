package zakhi.advent

import zakhi.advent.helpers.withResourceStream


fun main() {
    val (samples, commands) = withResourceStream("day 16 input.txt") {
        val text = it.bufferedReader().readText()
        parseSamples(text) to parseCommands(text)
    }

    val sampleCounts = samples.associateWith { sample -> operations.count { sample.matches(it) } }
    println("Part 1: ${sampleCounts.count { it.value >= 3 }}")

    val operationsByCode = assignOperationsToCodes(samples)

    val finalRegisters = commands.fold(initialRegisters) { registers, command ->
        registers.operate(command.input, operationsByCode[command.code]!!)
    }

    println("Part 2: ${finalRegisters.first()}")
}

fun parseSamples(input: String): List<Sample> {
    val regex = Regex("""Before: \[(\d+), (\d+), (\d+), (\d+)]\n(\d+) (\d+) (\d+) (\d+)\nAfter: {2}\[(\d+), (\d+), (\d+), (\d+)]""")

    return regex.findAll(input).map { match ->
        val values = match.groupValues.drop(1).map { it.toInt() }
        Sample(
            before = values.slice(0..3),
            code = values[4],
            input = Input(a = values[5], b = values[6], c = values[7]),
            after = values.slice(8..11)
        )
    }.toList()
}

fun parseCommands(input: String): List<Command> {
    val regex = Regex("""(\d+) (\d+) (\d+) (\d+)\n""")

    return regex.findAll(input, startIndex = input.indexOf("\n\n\n")).map { match ->
        val values = match.groupValues.drop(1).map { it.toInt() }
        Command(code = values[0], input = Input(a = values[1], b = values[2], c = values[3]))
    }.toList()
}

fun assignOperationsToCodes(samples: List<Sample>): Map<Int, Operation> {
    val operationsByCode = mutableMapOf<Int, Operation>()
    val sampleOperations = samples.associateWith { sample -> operations.filter { sample.matches(it) }.toMutableList() }.toMutableMap()

    while (sampleOperations.filter { it.value.isNotEmpty() }.isNotEmpty()) {
        val (sample, matchingOperations) = sampleOperations.entries.first { it.value.size == 1 }
        val operation = matchingOperations.first()

        operationsByCode[sample.code] = operation
        sampleOperations.values.forEach { it.remove(operation) }
    }

    return operationsByCode
}

data class Input(val a: Int, val b: Int, val c: Int)

data class Sample(val before: Registers, val code: Int, val input: Input, val after: Registers) {

    fun matches(operation: Operation) = before.operate(input, operation) == after
}

data class Command(val code: Int, val input: Input)

typealias Registers = List<Int>
typealias Operation = Registers.(Input) -> Int

val operations: List<Operation> = listOf(
    { (a, b) -> register(a) + register(b) },
    { (a, b) -> register(a) + b },
    { (a, b) -> register(a) * register(b) },
    { (a, b) -> register(a) * b },
    { (a, b) -> register(a) and register(b) },
    { (a, b) -> register(a) and b },
    { (a, b) -> register(a) or register(b) },
    { (a, b) -> register(a) or b },
    { (a) -> register(a) },
    { (a) -> a },
    { (a, b) -> if (a > register(b)) 1 else 0 },
    { (a, b) -> if (register(a) > b) 1 else 0 },
    { (a, b) -> if (register(a) > register(b)) 1 else 0 },
    { (a, b) -> if (a == register(b)) 1 else 0 },
    { (a, b) -> if (register(a) == b) 1 else 0 },
    { (a, b) -> if (register(a) == register(b)) 1 else 0 }
)

private fun Registers.register(index: Int) = get(index)

private fun Registers.operate(input: Input, operation: Operation): Registers =
    mapIndexed { index, value -> if (index == input.c) operation(input) else value }

val initialRegisters: Registers get() = listOf(0, 0, 0, 0)
