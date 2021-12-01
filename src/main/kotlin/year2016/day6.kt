package year2016

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.modalEntry

fun main() {
    val cols = readLinesFromFile("2016/day6.txt").let { input ->
        input[0].indices.map { index -> input.map { it[index] } }
    }

    cols.map { it.modalEntry() }
        .joinToString("")
        .also(displayPart1)

    cols
        .map { it.groupBy { it }.entries.minBy { it.value.size }!!.key }
        .joinToString("")
        .also(displayPart2)

}

