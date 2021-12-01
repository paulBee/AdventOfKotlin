package year2021

import isIncreasing
import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.pairedWithNext
import utils.collections.windowAverage

fun main() {

    val depths = readLinesFromFile("2021/day1.txt") { it.toDouble() }

    depths
        .pairedWithNext()
        .count { it.isIncreasing() }
        .also(displayPart1)


    depths
        .windowAverage(3)
        .pairedWithNext()
        .count { it.isIncreasing() }
        .also(displayPart2)
}


