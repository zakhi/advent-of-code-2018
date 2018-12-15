package zakhi.advent

import kotlin.math.absoluteValue


fun main() {
    println("Part 1: ${play(playerCount = 435, marbleCount = 71184)}")
    println("Part 2: ${play(playerCount = 435, marbleCount = 7118400)}")
}


fun play(playerCount: Int, marbleCount: Int): Long {
    val circle = Circle()

    val marbles = (1..marbleCount).asSequence()
    val players = (1..playerCount).map { Player() }

    marbles.zip(cycle(players)).forEach { (marble, player) ->
        player.score += circle.add(marble)
    }

    return players.map { it.score }.max()!!
}

fun <T> cycle(items: Iterable<T>): Sequence<T> = sequence {
    while (true) yieldAll(items)
}

data class Player(var score: Long = 0)

class Circle {

    private var current = Marble(0)

    fun add(value: Int): Long = when {
        value % 23 == 0 -> {
            move(-7)
            val removedValue = current.value
            current = current.remove()
            (value + removedValue).toLong()
        }
        else -> {
            move(2)
            current = current.addBefore(value)
            0L
        }
    }

    private fun move(times: Int) {
        repeat(times.absoluteValue) {
            current = if (times < 0) current.previous else current.next
        }
    }
}

data class Marble(val value: Int) {

    var previous: Marble = this
    var next: Marble = this

    fun addBefore(value: Int): Marble = Marble(value).also { newMarble ->
        newMarble.next = this
        newMarble.previous = this.previous
        this.previous.next = newMarble
        this.previous = newMarble
    }

    fun remove(): Marble {
        this.previous.next = this.next
        this.next.previous = this.previous
        return this.next
    }
}