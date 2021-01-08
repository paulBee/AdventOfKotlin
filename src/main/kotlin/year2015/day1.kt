package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readTextFromFile
import utils.collections.takeWhileInclusive

fun main() {
    readTextFromFile("2015/day1.txt")
        .map { when (it) {
            '(' -> 1
            ')' -> -1
            else -> 0
        } }
        .scan(0) { a, b -> a + b }.drop(1)
        .also { displayPart1(it.last()) }
        .also { displayPart2(it.takeWhileInclusive { it >= 0 }.count()) }
}