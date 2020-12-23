package year2020

import kotlinx.collections.immutable.*
import utils.aoc.displayPart1
import utils.aoc.displayPart2

fun main() {

    runCups(listOf(3,2,6,5,1,9,4,7,8)).elementAt(100)
        .second.startingAfter(1).takeWhile { it != 1 }.joinToString("")
        .also(displayPart1)

    runCups(listOf(3,2,6,5,1,9,4,7,8) + (10..1_000_000)).elementAt(10_000_000)
        .second.startingAfter(1).take(2).toList()
        .also { (first, second) -> displayPart2(first.toLong() * second.toLong()) }

}

fun runCups(initial: List<Int>): Sequence<Pair<Int, Map<Int, Int>>> {

    val rollingDecrementFrom = rollingDecrement(initial.maxOrNull()!!)

    return generateSequence(initial.first() to initial.asMapToNext())
    { (current, numberNextTo) ->

        val snippet = numberNextTo.startingAfter(current).take(3)
        val insertPoint = rollingDecrementFrom(current).first { it !in snippet }

        val newCurrent = numberNextTo[snippet.last()]!!

        val removeSnippet = current to numberNextTo[snippet.last()]!!
        val attachSnippet = snippet.last() to numberNextTo[insertPoint]!!
        val attachInsert = insertPoint to snippet.first()

        newCurrent to numberNextTo + mapOf(removeSnippet, attachSnippet, attachInsert)
    }
}

private fun List<Int>.asMapToNext() = (this + this.take(1)).zipWithNext().toMap().toPersistentHashMap()

private fun rollingDecrement(maxNumber: Int): (Int) -> Sequence<Int> =
    { start: Int -> generateSequence(start) { if (it == 1) maxNumber else it - 1 }.drop(1) }

private fun Map<Int, Int>.startingAfter(seed: Int): Sequence<Int> = generateSequence(seed) { this[it]!! }.drop(1)
