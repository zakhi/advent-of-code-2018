package zakhi.advent

import zakhi.advent.IntersectionTurn.*
import zakhi.advent.TrainDirection.*
import zakhi.advent.helpers.withResourceStream


fun main() {
    val (track, trains) = withResourceStream("day 13 input.txt") {
        parseTrackAndTrains(it.bufferedReader().lineSequence())
    }

    val (firstCollidedTrain, lastTrain) = moveTrains(track, trains)
    println("Part 1: ${firstCollidedTrain.position}")
    println("Part 2: ${lastTrain.position}")

}

fun parseTrackAndTrains(lines: Sequence<String>): Pair<Track, List<Train>> {
    val track = mutableMapOf<TrackPosition, Char>()
    val trains = mutableListOf<Train>()

    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            val position = TrackPosition(x, y)

            val trackChar = when (char) {
                '>', '<' -> '-'
                '^', 'v' -> '|'
                '|', '-', '\\', '/', '+' -> char
                else -> null
            }

            trackChar?.let { track[position] = it }
            TrainDirection.of(char)?.let { direction -> trains.add(Train(position, direction)) }
        }
    }

    return track to trains
}

fun moveTrains(track: Track, trains: List<Train>): Pair<Train, Train> {
    val remainingTrains = trains.toMutableList()
    var firstTrainCollided: Train? = null

    while (remainingTrains.size > 1) {
        for (train in remainingTrains.sorted()) {
            train.move(track)

            val trainCollided = remainingTrains.find { it !== train && it.position == train.position }

            if (trainCollided != null) {
                firstTrainCollided = firstTrainCollided ?: train
                remainingTrains.removeAll(listOf(train, trainCollided))
            }
        }
    }

    return firstTrainCollided!! to remainingTrains.first()
}


typealias Track = Map<TrackPosition, Char>

data class TrackPosition(val x: Int, val y: Int): Comparable<TrackPosition> {

    override fun compareTo(other: TrackPosition): Int = compareValuesBy(this, other, TrackPosition::y, TrackPosition::x)

    override fun toString() = "$x,$y"
}

enum class TrainDirection {
    LEFT, UP, RIGHT, DOWN;

    fun turnLeft(): TrainDirection = values()[(ordinal + values().size - 1) % values().size]
    fun turnRight(): TrainDirection = values()[(ordinal + 1) % values().size]

    companion object {
        fun of(char: Char): TrainDirection? = when (char) {
            '<' -> LEFT
            '^' -> UP
            '>' -> RIGHT
            'v' -> DOWN
            else -> null
        }
    }
}

enum class IntersectionTurn {
    TURN_LEFT, GO_STRAIGHT, TURN_RIGHT;

    companion object {
        fun iterator() = generateSequence(0) { (it + 1) % values().size }.map { values()[it] }.iterator()
    }
}

class Train(var position: TrackPosition, private var direction: TrainDirection) : Comparable<Train> {

    private val intersectionTurn = IntersectionTurn.iterator()

    fun move(track: Track) {
        position = when (direction) {
            LEFT -> position.copy(x = position.x - 1)
            UP -> position.copy(y = position.y - 1)
            RIGHT -> position.copy(x = position.x + 1)
            DOWN -> position.copy(y = position.y + 1)
        }

        direction = when (track[position]) {
            '/' -> when (direction) {
                UP, DOWN -> direction.turnRight()
                else -> direction.turnLeft()
            }
            '\\' -> when (direction) {
                UP, DOWN -> direction.turnLeft()
                else -> direction.turnRight()
            }
            '+' -> when (intersectionTurn.next()) {
                TURN_LEFT -> direction.turnLeft()
                TURN_RIGHT -> direction.turnRight()
                GO_STRAIGHT -> direction
            }
            else -> direction
        }
    }

    override fun compareTo(other: Train): Int = compareValuesBy(this, other, Train::position)
}
