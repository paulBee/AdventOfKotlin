package year2019

import utils.aoc.readTextFromFile
import kotlin.math.abs

fun main() {
    val initialPhase = readTextFromFile("2019/day16.txt").toList().map { it.toString().toInt() }
    part1(initialPhase)
    part2(initialPhase)

}

private fun part2(initialPhase: List<Int>) {
    val bigInput = (1..10000).flatMap { initialPhase }
    val amountToDrop = bigInput.take(7).joinToString("").toInt()
    val simplifiedInput = bigInput.drop(amountToDrop).toList()

    var phase = 0
    val phases = generateSequence(simplifiedInput.reversed()) {
        println(phase++)
        nextPhaseSumming(it)
    }
        .drop(1)
        .take(100)

    println("The first 8 digits of the 100th phase is ${phases.last().reversed().joinToString("").substring((0 until 8))}")

}

fun nextPhaseSumming(lastPhase: List<Int>): List<Int> {
    val size = lastPhase.size

    return sequence {
        var acc = 0
        lastPhase.forEach {
            acc += it
            yield(acc % 10)
        }
    }.take(size).toList()
}

fun valueForPositionSumming(position: Int, lastPhase: List<Int>) =
    lastPhase.drop(position - 1)
        .sum()
        .let {
            abs(it % 10)
        }


private fun part1(initialPhase: List<Int>) {
    val phases = generateSequence(initialPhase) { nextPhase(it) }
        .drop(1)
        .take(100)

    println("The first 8 digits of the 100th phase is ${phases.last().joinToString("").substring((0 until 8))}")
}

fun nextPhase(lastPhase: List<Int>): List<Int> {
    val size = lastPhase.size

   return (1..size).map { position -> valueForPosition(position, lastPhase) }
}

fun valueForPosition(position: Int, lastPhase: List<Int>) : Int {
    val pattern = phaseSequence(position).take(lastPhase.size).toList()

    return lastPhase
        .mapIndexed { index, oldValue -> oldValue * pattern[index] }
        .sum()
        .let {
            abs(it % 10)
        }
}

fun phaseSequence(position: Int): Sequence<Int> {
    val repeatingElements = listOf(0, 1, 0, -1).flatMap { phase -> (0 until position).map { phase } }

    return sequence {
        while (true) {
            repeatingElements.forEach { yield(it) }
        }
    }.drop(1)
}
