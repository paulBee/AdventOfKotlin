package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.collections.takeWhileInclusive
import kotlin.math.sqrt

fun main() {
    generateSequence(1) { it + 1 }
        .map { it.presents() }
        .takeWhileInclusive { it < 36000000 }
        .also { displayPart1(it.toList().size) }

    generateSequence(1) { it + 1 }
        .map { it.presents2() }
        .takeWhileInclusive { it < 36000000 }
        .also { displayPart2(it.toList().size) }
}

private fun Int.presents() = (1..sqrt(this.toDouble()).toInt())
    .flatMap { if (this % it == 0) listOf(it, this / it) else emptyList() }
    .sum() * 10

private fun Int.presents2() = (1..sqrt(this.toDouble()).toInt())
    .flatMap { if (this % it == 0) listOf(it, this / it) else emptyList() }
    .filter { it * 50 >= this }
    .sum() * 11