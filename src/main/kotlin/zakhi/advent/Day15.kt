package zakhi.advent

import zakhi.advent.helpers.Position
import zakhi.advent.helpers.withResourceStream


fun main() {
    val battlefield: BattleField = withResourceStream("day 15 input.txt") {
        val input = it.bufferedReader().lineSequence().withIndex().flatMap { (y, line) ->
            line.withIndex().asSequence().map { (x, char) -> Position(x, y) to char }
        }.toMap()

        createBattlefield(input)
    }

    battlefield.runBattle()
    println("Part 1: ${battlefield.completedRounds * battlefield.totalHitPointsLeft}")
}


private fun createBattlefield(input: Map<Position, Char>): BattleField {
    val squaresByPosition = input.filter { it.value != '#' }.mapValues { BattleFieldSquare(it.key) }

    squaresByPosition.values.forEach { square ->
        square.adjacentSquares = square.position.adjacentPositions.mapNotNull { squaresByPosition[it] }.sortedBy { it.position }
    }

    input.filter { it.value in listOf('G', 'E') }.forEach { (position, char) ->
        val unit = if (char == 'G') Goblin() else Elf()
        unit.moveTo(squaresByPosition[position]!!)
    }

    return BattleField(squaresByPosition.values.sortedBy { it.position })
}


class BattleField(private val squares: List<BattleFieldSquare>) {

    var completedRounds = 0
    val totalHitPointsLeft get() = units.sumBy { it.hitPoints }

    private val units get() = squares.mapNotNull { it.unit }.sorted()

    private val elfLeft get() = units.any { it is Elf }
    private val goblinLeft get() = units.any { it is Goblin }

    fun runBattle() {
        while (elfLeft && goblinLeft) {
            for (unit in units) {
                if (!unit.dead) {
                    if (!unit.hasEnemies) return

                    unit.moveCloserToNearestEnemy()
                    unit.attackNearestEnemy()
                }
            }

            completedRounds++
            printMe()
        }
    }

    private val BattleUnit.hasEnemies: Boolean get() = units.any { isEnemy(it) }

    private fun printMe() {
        println("After round $completedRounds")
        val squaresByPosition = squares.associateBy { it.position }

        for (y in 0..squares.map { it.position.y }.max()!!) {
            for (x in 0..squares.map { it.position.x }.max()!!) {
                val square = squaresByPosition[Position(x, y)]

                if (square == null) {
                    print('#')
                } else {
                    print(
                        when (square.unit) {
                            is Elf -> 'E'
                            is Goblin -> 'G'
                            null -> '.'
                        }
                    )
                }
            }

            println()
        }

        println()
    }
}


class BattleFieldSquare(val position: Position) {

    var adjacentSquares: List<BattleFieldSquare> = emptyList()
    var unit: BattleUnit? = null

    fun scan(): Sequence<BattleFieldSquare> = sequence {
        yield(this@BattleFieldSquare)
        val visitedSquares = mutableSetOf(this@BattleFieldSquare)

        while (true) {
            val nextSquares = visitedSquares.flatMap { it.adjacentSquares }
                .filter { it.unit == null }
                .filterNot { it in visitedSquares }
                .sortedBy { it.position }

            if (nextSquares.isEmpty()) break

            yieldAll(nextSquares)
            visitedSquares.addAll(nextSquares)
        }
    }
}

sealed class BattleUnit : Comparable<BattleUnit> {

    var hitPoints = 200
        private set

    val dead: Boolean get() = hitPoints <= 0

    abstract fun isEnemy(other: BattleUnit?): Boolean

    private var square: BattleFieldSquare? = null

    override fun compareTo(other: BattleUnit): Int = compareValuesBy(this, other) { it.square?.position }

    fun moveCloserToNearestEnemy() {
        val closestAttackSquare = checkNotNull(square).scan().find { it.adjacentSquares.any { isEnemy(it.unit) } }
        closestAttackSquare.takeIf { it != this.square }?.let { moveTowards(it) }
    }

    fun attackNearestEnemy() {
        checkNotNull(square).adjacentSquares.mapNotNull { it.unit }.minBy { it.hitPoints }?.hit()
    }

    fun moveTo(square: BattleFieldSquare) {
        if (this.square == square) return

        this.square?.unit = null
        this.square = square
        square.unit = this
    }

    private fun moveTowards(square: BattleFieldSquare) {
        val thisSquare = checkNotNull(this.square)
        square.scan().find { it in thisSquare.adjacentSquares }?.let { moveTo(it) }
    }

    private fun hit() {
        hitPoints -= 3

        if (dead) {
            this.square?.unit = null
        }
    }
}

class Elf : BattleUnit() {
    override fun isEnemy(other: BattleUnit?): Boolean = other is Goblin
}

class Goblin : BattleUnit() {
    override fun isEnemy(other: BattleUnit?): Boolean = other is Elf
}
