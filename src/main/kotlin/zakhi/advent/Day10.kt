package zakhi.advent

import zakhi.advent.helpers.withResourceStream
import kotlin.math.absoluteValue


fun main() {
    val points = withResourceStream("day 10 input.txt") { it.bufferedReader().readLines().map(::toLightPoint) }
    val arrangement = waitForMessage(points)

    println("Part 1:")
    arrangement.print()

    println("Part 2: ${arrangement.time}")
}

fun toLightPoint(line: String): LightPoint {
    val match = Regex("""position=<\s*([-\d]+),\s*([-\d]+)> velocity=<\s*([-\d]+),\s*([-\d]+)>""").matchEntire(line)
        ?: throw Exception("Invalid line $line")

    val values = match.groupValues.drop(1).map { it.toInt() }
    return LightPoint(Position(values[0], values[1]), Velocity(values[2], values[3]))
}


fun waitForMessage(points: Collection<LightPoint>): LightPointArrangement {
    var arrangement = LightPointArrangement(points)

    while (true) {
        val newArrangement = arrangement.move()

        if (newArrangement.spread > arrangement.spread) {
            return arrangement
        }

        arrangement = newArrangement
    }
}

data class Position(val x: Int, val y: Int)

data class Velocity(val dx: Int, val dy: Int)

data class Spread(val x: Int, val y: Int): Comparable<Spread> {

    override fun compareTo(other: Spread): Int = when {
        this == other -> 0
        this.x > other.x || this.y > other.y -> 1
        else -> -1
    }
}

data class LightPoint(val position: Position, val velocity: Velocity) {

    fun move(): LightPoint = copy(position = Position(position.x + velocity.dx, position.y + velocity.dy))
}

class LightPointArrangement(private val points: Collection<LightPoint>, val time: Int = 0) {

    private val xs = points.map { it.position.x }.distinct()
    private val ys = points.map { it.position.y }.distinct()

    val spread: Spread get() = Spread((xs.max()!! - xs.min()!!).absoluteValue, (ys.max()!! - ys.min()!!).absoluteValue)

    fun move() = LightPointArrangement(points.map { it.move() }, time + 1)

    fun print() {
        val positions = points.map { it.position }.toSet()

        for (y in ys.min()!!..ys.max()!!) {
            for (x in xs.min()!!..xs.max()!!) {
                print(if (Position(x, y) in positions) "#" else ".")
            }

            println()
        }
    }
}
