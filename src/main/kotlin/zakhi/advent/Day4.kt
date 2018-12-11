package zakhi.advent

import zakhi.advent.helpers.withResourceStream
import java.time.LocalDateTime


fun main() {
    val lines = withResourceStream("day 4 input.txt") { it.bufferedReader().readLines() }
    val records = lines.sorted().map(::parseRecord)

    val guards = mutableMapOf<Int, Guard>()
    var currentGuard: Guard? = null
    var lastFellAsleep: Int? = null

    for (record in records) {
        when (record) {
            is BeginShiftRecord -> currentGuard = guards.computeIfAbsent(record.guardId) { Guard(it) }
            is FellAsleepRecord -> lastFellAsleep = record.minute
            is WokeUpRecord -> currentGuard!!.addSleepRecord(lastFellAsleep!! until record.minute)
        }
    }

    val bestGuard = guards.values.maxBy { it.totalMinutesAsleep } ?: throw Exception("Could not find best guard")
    val mostSleepOnSingleMinuteGuard = guards.values.maxBy { it.timesSleptOnMostFrequentMinute } ?: throw Exception("Could not find most sleep on single minute")

    println("Part 1: ${bestGuard.id * bestGuard.minuteSleptMost}")
    println("Part 2: ${mostSleepOnSingleMinuteGuard.id * mostSleepOnSingleMinuteGuard.minuteSleptMost}")
}

sealed class Record

class BeginShiftRecord(val guardId: Int) : Record()

class FellAsleepRecord(val minute: Int) : Record()

class WokeUpRecord(val minute: Int) : Record()


private val pattern = Regex("""\[(\d{4}-\d{2}-\d{2}) (\d{2}:\d{2})] (.+)""")
private val beginShiftPattern = Regex("""Guard #(\d+) begins shift""")

fun parseRecord(line: String): Record {
    val match = pattern.matchEntire(line) ?: throw Exception("Invalid line: $line")
    val minute = LocalDateTime.parse("${match.groupValues[1]}T${match.groupValues[2]}").minute
    val text = match.groupValues[3]

    return when (text) {
        "wakes up" -> WokeUpRecord(minute)
        "falls asleep" -> FellAsleepRecord(minute)
        else -> {
            val beginShiftMatch = beginShiftPattern.matchEntire(text) ?: throw Exception("invalid line: $line")
            BeginShiftRecord(beginShiftMatch.groupValues[1].toInt())
        }
    }
}

class Guard(val id: Int) {

    private val sleepRecords = mutableListOf<IntRange>()

    fun addSleepRecord(minutes: IntRange) = sleepRecords.add(minutes)

    val totalMinutesAsleep get() = sleepRecords.sumBy { it.last - it.first }

    val minuteSleptMost get() = (0..59).maxBy { minute -> sleepRecords.count { minute in it } } ?: throw Exception("Guard $this did not sleep")

    val timesSleptOnMostFrequentMinute get() = (0..59).map { minute -> sleepRecords.count { minute in it } }.max() ?: throw Exception("Guard $this did not sleep")
}
