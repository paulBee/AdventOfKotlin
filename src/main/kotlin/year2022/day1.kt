package year2022

import utils.aoc.readLinesFromFile
import utils.collections.chunkOnEmptyLine

fun main() {

    val elfs = readLinesFromFile("2022/day1.txt").chunkOnEmptyLine()

    val elfTotals = elfs
        .map { it.sumBy(String::toInt) }
        .sorted()

    println(elfTotals.last())
    println(elfTotals.takeLast(3).sum())

}