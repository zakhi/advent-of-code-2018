package zakhi.advent

import zakhi.advent.helpers.withResourceStream
import kotlin.math.absoluteValue
import kotlin.streams.toList


fun main() {
    val coordinates = loadCoordinates()

    val topLeft = Point(coordinates.map { it.x }.min()!!, coordinates.map { it.y }.min()!!)
    val bottomRight = Point(coordinates.map { it.x }.max()!!, coordinates.map { it.y }.max()!!)

    val borderPoints = mutableSetOf<Point>()
    var safeAreaSize = 0

    allPointBetween(topLeft, bottomRight).forEach { point ->
        val coordinatesByDistance = coordinates.groupBy { it.distanceFrom(point) }
        val closestCoordinates = coordinatesByDistance.minBy { it.key }!!.value

        if (closestCoordinates.size == 1) {
            closestCoordinates.first().addClosestPoint(point)
        }

        if (point.isBorder(topLeft, bottomRight)) {
            borderPoints.add(point)
        }

        val totalDistance = coordinatesByDistance.map { it.key * it.value.size }.sum()

        if (totalDistance < 10_000) {
            safeAreaSize++
        }
    }

    fun Coordinate.hasInfiniteArea() = closestPoints.any { it in borderPoints }
    val enclosedCoordinates = coordinates.filterNot { it.hasInfiniteArea() }

    println("Part 1: ${enclosedCoordinates.map { it.area }.max()}")
    println("Part 2: $safeAreaSize")
}


data class Point(val x: Int, val y: Int) {

    fun isBorder(topLeft: Point, bottomRight: Point): Boolean =
        x == topLeft.x || x == bottomRight.x || y == topLeft.y || y == bottomRight.y
}


class Coordinate(val x: Int, val y: Int) {

    val closestPoints = mutableListOf<Point>()

    val area: Int get() = closestPoints.size

    fun distanceFrom(point: Point): Int = (point.x - x).absoluteValue + (point.y - y).absoluteValue

    fun addClosestPoint(point: Point) = closestPoints.add(point)
}

fun loadCoordinates(): List<Coordinate> =
    withResourceStream("day 6 input.txt") { it.bufferedReader().lineSequence().map(::toCoordinate).toList() }

fun toCoordinate(line: String): Coordinate {
    val match = Regex("""(\d+), (\d+)""").matchEntire(line) ?: throw Exception("Invalid line: $line")
    return Coordinate(match.groupValues[1].toInt(), match.groupValues[2].toInt())
}

fun allPointBetween(topLeft: Point, bottomRight: Point): Sequence<Point> = sequence {
    for (x in topLeft.x..bottomRight.x) {
        for (y in topLeft.y..bottomRight.y) {
            yield(Point(x, y))
        }
    }
}
