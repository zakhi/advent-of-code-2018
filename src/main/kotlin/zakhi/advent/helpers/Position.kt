package zakhi.advent.helpers


data class Position(val x: Int, val y: Int): Comparable<Position> {

    override fun compareTo(other: Position): Int = compareValuesBy(this, other, Position::y, Position::x)

    val right get() = copy(x = x + 1)
    val left get() = copy(x = x - 1)
    val up get() = copy(y = y - 1)
    val down get() = copy(y = y + 1)

    val adjacentPositions get() = listOf(up, left, right, down)
}
