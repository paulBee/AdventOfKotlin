package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readTextFromFile

fun main() {
    val starter = readTextFromFile("2020/day15.txt").split(",").map { it.toLong() }

    sequenceFrom(starter).take(2020).last().also(displayPart1)
    sequenceFrom(starter).take(30_000_000).last().also(displayPart2)
}

fun sequenceFrom(startingList: List<Long>): Sequence<Long> {
    val history = mutableMapOf<Long, Long>()
    var iteration = 0L
    var lastNumber = startingList[0]

    return generateSequence()
    {
        when (iteration) {
            in startingList.indices -> startingList[iteration.toInt()]
            else -> history[lastNumber]?.let { iteration - it } ?: 0L
        }.also {
            history[lastNumber] = iteration
            lastNumber = it
            iteration++
        }
    }
}