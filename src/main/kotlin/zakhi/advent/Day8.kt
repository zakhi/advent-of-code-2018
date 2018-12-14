package zakhi.advent

import zakhi.advent.helpers.withResourceStream


fun main() {
    val numbers = readNumbers()

    println("Part 1: ${totalMetadata(numbers.iterator())}")
    println("Part 2: ${nodeValue(numbers.iterator())}")
}

private fun readNumbers(): List<Int> {
    val text = withResourceStream("day 8 input.txt") { it.bufferedReader().readText() }
    return Regex("""\d+""").findAll(text).map { it.value.toInt() }.toList()
}

private fun totalMetadata(numbers: Iterator<Int>): Int {
    val childNodes = numbers.next()
    val metadataNodes = numbers.next()

    val childMetadataTotal = (1..childNodes).map { totalMetadata(numbers) }.sum()
    val nodeMetadataTotal = (1..metadataNodes).map { numbers.next() }.sum()

    return childMetadataTotal + nodeMetadataTotal
}

fun nodeValue(numbers: Iterator<Int>): Int {
    val childNodes = numbers.next()
    val metadataNodes = numbers.next()

    val childMetadata = (1..childNodes).associate { it to nodeValue(numbers) }
    val metadataEntries = (1..metadataNodes).map { numbers.next() }

    return when (childNodes) {
        0 -> metadataEntries.sum()
        else -> metadataEntries.map { childMetadata[it] ?: 0 }.sum()
    }
}
