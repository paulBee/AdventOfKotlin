package year2015

import kotlinx.collections.immutable.persistentHashMapOf
import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.sumLongBy

fun main() {

    val instructions = readLinesFromFile("2015/day6.txt").map { it.parsed() }

    instructions
        .fold(persistentHashMapOf<Pair<Int,Int>,Boolean>())
        { acc, next -> next.points.fold(acc) { tmp, point -> tmp.put(point, updater1(next.instr, acc.getOrDefault(point, false))) } }
        .count { it.value }
        .also(displayPart1)

    instructions
        .fold(persistentHashMapOf<Pair<Int,Int>,Long>())
        { acc, next -> next.points.fold(acc) { tmp, point -> tmp.put(point, updater2(next.instr, acc.getOrDefault(point, 0L))) } }
        .values.sumLongBy { it }
        .also(displayPart2)

}

private fun updater1(
    instruction: String,
    previous: Boolean
) = when (instruction) {
    "toggle" -> !previous
    "turn off" -> false
    "turn on" -> true
    else -> throw RuntimeException("Boom!")
}

private fun updater2(
    instruction: String,
    previous: Long
) = when (instruction) {
    "toggle" -> previous + 2
    "turn off" -> maxOf(0, previous - 1)
    "turn on" -> previous + 1
    else -> throw RuntimeException("Boom!")
}

private val MatchResult.Destructured.instr
    get() = component1()

private val MatchResult.Destructured.points: Set<Pair<Int,Int>>
      get() = this.let { (_, xLower, yLower, xHigher, yHigher) -> ((xLower.toInt() to yLower.toInt()) through (xHigher.toInt() to yHigher.toInt()))}

private val regex = "(toggle|turn off|turn on) (\\d+),(\\d+) through (\\d+),(\\d+)".toRegex()
private fun String.parsed() = regex.matchEntire(this)?.destructured!!


infix fun Pair<Int, Int>.through(other: Pair<Int, Int>): Set<Pair<Int,Int>> =
    (this.first..other.first).flatMap { x -> (this.second..other.second).map { y -> x to y } }.toSet()
