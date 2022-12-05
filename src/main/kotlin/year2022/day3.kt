package year2022

import utils.aoc.readLinesFromFile

fun main() {
    val input = readLinesFromFile("2022/day3.txt")
    val rucksacks = input.map { it.toCharArray() }

    rucksacks.map { it.take(it.size / 2).intersect(it.takeLast(it.size / 2).toSet()) }
        .sumBy { it.first().toPriority() }
        .also { println(it) }

    rucksacks.chunked(3)
        .map { it[0].intersect(it[1].toSet()).intersect(it[2].toSet()).first() }
        .sumBy { it.toPriority() }
        .also { println(it) }
}

fun Char.toPriority() = if (this.isUpperCase()) this.toInt() + (27 - 65) else this.toInt() + (1 - 97)