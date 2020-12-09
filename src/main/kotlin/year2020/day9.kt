package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile

fun main () {

    val cypher = readLinesFromFile("2020/day9.txt").map(String::toLong)

    val invalid = cypher.findInvalid().also(displayPart1)

    cypher.findContiguousSummingTo(invalid)?.also { displayPart2(it.minOrNull()!! + it.maxOrNull()!!) }
}

typealias Cypher = List<Long>

fun Cypher.findInvalid() =
    this.windowed(26)
        .first { findNumbersSummingTo(it.last(), it.dropLast(1), 2) == null }
        .last()

fun Cypher.findContiguousSummingTo(number: Long): List<Long>? =
    when (this.size) {
        0 -> null
        else -> this.startingSliceSummingTo(number) ?: this.drop(1).findContiguousSummingTo(number)
    }

fun List<Long>.startingSliceSummingTo(sumTotal: Long): List<Long>? =
    generateSequence(0) { it + 1 }
        .map { this.take(it) }
        .takeWhile { it.sum() <= sumTotal }
        .firstOrNull { it.sum() == sumTotal }


