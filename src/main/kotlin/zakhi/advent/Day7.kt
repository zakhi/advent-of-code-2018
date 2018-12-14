package zakhi.advent

import zakhi.advent.helpers.withResourceStream


fun main() {
    val dependencies = withResourceStream("day 7 input.txt") {
        it.bufferedReader().lineSequence().map(::toDependency).toList()
    }

    println("Part 1: ${sequentialBuildSequence(createSteps(dependencies))}")
    println("Part 2: ${totalBuildTimeInParallel(createSteps(dependencies))}")
}

private fun toDependency(line: String): Dependency {
    val match = Regex("""Step (\w) must be finished before step (\w) can begin.""").matchEntire(line) ?: throw Exception("Invalid line: $line")
    return Dependency(match.groupValues[2].first(), dependsOn = match.groupValues[1].first())
}

private fun createSteps(dependencies: List<Dependency>): Collection<Step> {
    val steps = dependencies.flatMap { listOf(it.step, it.dependsOn) }.distinct().associateWith { letter -> Step(letter) }
    dependencies.forEach { steps[it.step]!!.dependOn(steps[it.dependsOn]!!) }
    return steps.values
}

fun sequentialBuildSequence(steps: Collection<Step>): String {
    val orderedSteps = sequence {
        while (!steps.all { it.completed }) {
            val nextStep = steps.filter { it.canRun }.minBy { it.letter } ?: throw Exception("Cannot find next step")
            yield(nextStep)
            nextStep.build()
        }
    }

    return orderedSteps.map { it.letter }.joinToString(separator = "")
}

fun totalBuildTimeInParallel(steps: Collection<Step>): Int {
    val stepsToBuild = steps.toMutableSet()
    val workers = (1..5).map { Worker() }
    var timePassed = 0

    while (!steps.all { it.completed }) {
        val availableWorkers = workers.filter { it.isAvailable }
        val availableSteps = stepsToBuild.filter { it.canRun }.sortedBy { it.letter }

        availableWorkers.zip(availableSteps).forEach { (worker, step) ->
            worker.start(step)
            stepsToBuild.remove(step)
        }

        workers.forEach { it.workOneSecond() }
        timePassed += 1
    }

    return timePassed
}


data class Dependency(val step: Char, val dependsOn: Char)

data class Step(val letter: Char) {

    private val dependencies = mutableSetOf<Step>()
    private var time = 60 + (letter - 'A' + 1)

    val completed: Boolean get() = time == 0

    fun dependOn(step: Step) {
        dependencies.add(step)
    }

    fun build(amount: Int = time) {
        if (!completed) time -= amount
    }

    val canRun: Boolean get() = !completed && dependencies.all { it.completed }
}

class Worker {

    private var step: Step? = null

    val isAvailable: Boolean get() = step?.completed ?: true

    fun start(step: Step) {
        this.step = step
    }

    fun workOneSecond() {
        step?.build(amount = 1)
    }
}
