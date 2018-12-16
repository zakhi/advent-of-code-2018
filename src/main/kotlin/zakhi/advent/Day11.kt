package zakhi.advent

import zakhi.advent.helpers.cartesianProduct


const val serialNumber = 7315

fun main() {
    val grid = Matrix(300) { x, y ->
        val rackId = 10 + x
        val totalPowerLevel = (rackId * y + serialNumber) * rackId
        (totalPowerLevel / 100) % 10
    }

    val bestSquare = grid.subGrids(size = 3).maxBy { it.sum }!!
    println("Part 1: ${bestSquare.topLeft.x},${bestSquare.topLeft.y}")
}


data class GridCoordinates(val x: Int, val y: Int)

interface Grid {

    val topLeft: GridCoordinates
    val sum: Int

    operator fun get(x: Int, y: Int): Int
}

class Matrix(private val side: Int, private val getValue: (Int, Int) -> Int) : Grid {

    private val valueByCoordinates: Map<GridCoordinates, Int>

    init {
        val sideRange = 1..side
        valueByCoordinates = (sideRange cartesianProduct sideRange).associate { (x, y) -> GridCoordinates(x, y) to getValue(x, y) }
    }

    override val topLeft = GridCoordinates(1, 1)

    override val sum: Int get() = valueByCoordinates.values.sum()

    override operator fun get(x: Int, y: Int): Int = valueByCoordinates[GridCoordinates(x, y)] ?:
        throw Exception("Invalid coordinates ($x, $y)")

    fun subGrids(size: Int): Sequence<Grid> = sequence {
        val squareSide = side - size + 1

        for (x in 1..squareSide) {
            for (y in 1..squareSide) {
                yield(subGrid(x, y, size))
            }
        }
    }

    private fun subGrid(x: Int, y: Int, size: Int): Grid = object : Grid {
        override val topLeft = GridCoordinates(x, y)

        override val sum: Int
            get() {
                val side = 1..size
                return (side cartesianProduct side).sumBy { (x, y) -> this[x, y] }
            }

        override fun get(x: Int, y: Int): Int = this@Matrix[topLeft.x + x - 1, topLeft.y + y - 1]
    }
}
