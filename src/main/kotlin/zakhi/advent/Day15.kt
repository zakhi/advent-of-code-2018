package zakhi.advent

import zakhi.advent.helpers.Position
import zakhi.advent.helpers.withResourceStream


fun main() {
    val input = withResourceStream("day 15 input.txt") {
        it.bufferedReader().lineSequence().withIndex().flatMap { (y, line) ->
            line.withIndex().asSequence().map { (x, char) -> Position(x, y) to char }
        }.toMap()
    }

    val originalBattleField = createBattleField(input)
    originalBattleField.runFullBattle()
    println("Part 1: ${originalBattleField.result}")

    val elvesWonBattleField = generateSequence(4) { it + 1 }
        .map { createBattleField(input, elfAttackPower = it) }
        .first { it.runBattleWithoutElfDying() }

    println("Part 2: ${elvesWonBattleField.result}")
}

private fun createBattleField(input: Map<Position, Char>, elfAttackPower: Int = 3): BattleField {
    val squaresByPosition = input.filter { it.value != '#' }.mapValues { BattleFieldSquare(it.key) }

    squaresByPosition.values.forEach { square ->
        square.adjacentSquares = square.position.adjacentPositions.mapNotNull { squaresByPosition[it] }
    }

    input.filter { it.value in listOf('G', 'E') }.forEach { (position, char) ->
        val unit = if (char == 'G') Goblin() else Elf(elfAttackPower)
        unit.moveTo(squaresByPosition[position]!!)
    }

    return BattleField(squaresByPosition.values.sortedBy { it.position })
}


class BattleField(private val squares: List<BattleFieldSquare>) {

    val result get() = completedRounds * units.sumBy { it.hitPoints }

    private var completedRounds = 0

    private val units get() = squares.mapNotNull { it.unit }
    private val elfLeft get() = units.any { it is Elf }
    private val goblinLeft get() = units.any { it is Goblin }
    private val numberOfElves get() = units.count { it is Elf }

    fun runFullBattle() {
        while (elfLeft && goblinLeft) {
            val completed = runNextBattleRound()
            if (!completed) return
        }
    }

    fun runBattleWithoutElfDying(): Boolean {
        val elvesStarting = numberOfElves
        val allElvesSurvived = { elvesStarting == numberOfElves }

        while (goblinLeft || allElvesSurvived()) {
            val completed = runNextBattleRound()
            if (!completed) return allElvesSurvived()
        }

        return allElvesSurvived()
    }

    private fun runNextBattleRound(): Boolean {
        for (unit in units) {
            if (!unit.dead) {
                if (!unit.hasEnemies) return false

                unit.moveCloserToNearestEnemy()
                unit.attackNearbyEnemy()
            }
        }

        completedRounds++
        return true
    }

    private val BattleUnit.hasEnemies: Boolean get() = units.any { isEnemyOf(it) }
}

class BattleFieldSquare(val position: Position) {

    lateinit var adjacentSquares: List<BattleFieldSquare>

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

sealed class BattleUnit(private val attackPower: Int = 3) : Comparable<BattleUnit> {

    var hitPoints = 200
        private set

    val dead: Boolean get() = hitPoints <= 0

    abstract fun isEnemyOf(other: BattleUnit?): Boolean

    private var square: BattleFieldSquare? = null

    override fun compareTo(other: BattleUnit): Int = compareValuesBy(this, other) { it.square?.position }

    fun moveCloserToNearestEnemy() {
        val closestAttackSquare = checkNotNull(square).scan().find { it.nearbyEnemies.isNotEmpty() }
        closestAttackSquare.takeIf { it != this.square }?.let { moveTowards(it) }
    }

    fun attackNearbyEnemy() {
        checkNotNull(square).nearbyEnemies.minBy { it.hitPoints }?.hit(attackPower)
    }

    fun moveTo(square: BattleFieldSquare) {
        this.square?.unit = null
        this.square = square
        square.unit = this
    }

    private val BattleFieldSquare.nearbyEnemies get() = adjacentSquares.mapNotNull { it.unit }.filter { isEnemyOf(it) }

    private fun moveTowards(square: BattleFieldSquare) {
        val thisSquare = checkNotNull(this.square)
        square.scan().find { it in thisSquare.adjacentSquares }?.let { moveTo(it) }
    }

    private fun hit(power: Int) {
        hitPoints -= power

        if (dead) {
            this.square?.unit = null
        }
    }
}

class Elf(attackPower: Int) : BattleUnit(attackPower) {
    override fun isEnemyOf(other: BattleUnit?): Boolean = other is Goblin
}

class Goblin : BattleUnit() {
    override fun isEnemyOf(other: BattleUnit?): Boolean = other is Elf
}
