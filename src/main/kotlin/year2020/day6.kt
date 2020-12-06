package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.chunkOnEmptyLine

fun main() {
    val groups = readLinesFromFile("2020/day6.txt")
        .chunkOnEmptyLine()
        .map { person -> person.map { it.asSequence().toSet()} }

    groups.sumBy { group -> group.groupBy { it }.keys.size }.also(displayPart1)
    groups.sumBy { group -> group.reduce { acc, next -> acc.intersect(next) }.size }.also(displayPart2)
}

