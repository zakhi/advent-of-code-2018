package zakhi.advent


const val SERIAL_NUMBER = 7315
const val GRID_SIZE = 300


fun main() {
    val matrix = Matrix()
    matrixIndices().forEach { (x, y) -> matrix[x, y] = powerLevel(x, y) }

    val sumMatrix = Matrix()
    matrixIndices().forEach { (x, y) ->
        sumMatrix[x, y] = matrix[x, y] + sumMatrix[x - 1, y] + sumMatrix[x, y - 1] - sumMatrix[x - 1, y - 1]
    }

    fun Square.sum(): Int {
        val (x1, y1) = topLeft
        val (x2, y2) = bottomRight
        return sumMatrix[x2, y2] - sumMatrix[x1 - 1, y2] - sumMatrix[x2, y1 - 1] + sumMatrix[x1 - 1, y1 - 1]
    }

    val top3By3Square = squares(side = 3).maxBy { it.sum() }
    println("Part 1: ${top3By3Square?.run { "$x,$y" }}")

    val topSquare = (1..GRID_SIZE).asSequence().flatMap { side -> squares(side) }.maxBy { it.sum() }
    println("Part 2: ${topSquare?.run { "$x,$y,$side" }}")
}

fun matrixIndices(side: Int = GRID_SIZE): Sequence<Pair<Int, Int>> =
    (1..side).asSequence().flatMap { y -> (1..side).asSequence().map { x -> x to y } }

fun squares(side: Int): Sequence<Square> = matrixIndices(GRID_SIZE - side + 1).map { (x, y) -> Square(x, y, side) }

fun powerLevel(x: Int, y: Int): Int {
    val rackId = 10 + x
    val totalPowerLevel = (rackId * y + SERIAL_NUMBER) * rackId
    return (totalPowerLevel / 100) % 10 - 5
}

class Matrix {

    private val values = mutableMapOf<Pair<Int, Int>, Int>()

    operator fun get(x: Int, y: Int): Int = values[x to y] ?: 0

    operator fun set(x: Int, y: Int, value: Int) {
        require(x in 1..GRID_SIZE && y in 1..GRID_SIZE) { "($x, $y) out of bounds" }
        values[x to y] = value
    }
}

data class Square(val x: Int, val y: Int, val side: Int) {
    val topLeft get() = x to y
    val bottomRight get() = (x + side - 1) to (y + side - 1)
}
