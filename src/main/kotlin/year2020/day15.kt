package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readTextFromFile

fun main() {
    val starter = readTextFromFile("2020/day15.txt").split(",").map { it.toInt() }

    sequenceFrom(starter).take(2020).last().also(displayPart1)
    sequenceFrom(starter).take(30_000_000).last().also(displayPart2)
}

fun sequenceFrom(seedList: List<Int>) = sequence {

    val lastSeenOn = seedList
        .mapIndexed { index, value -> value to index }
        .toMap().toMutableMap()

    seedList.forEach { yield(it) }

    var iteration = seedList.indices.last
    var lastNumber = seedList.last()

    while (true) {
        val nextNumber = lastSeenOn[lastNumber]?.let { iteration - it } ?: 0
        lastSeenOn[lastNumber] = iteration
        yield(nextNumber)
        lastNumber = nextNumber
        iteration++
    }
}