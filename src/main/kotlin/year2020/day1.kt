package year2020

import utils.aoc.displayPart1
import utils.aoc.readLinesFromFile
import utils.collections.multiply
import utils.collections.scanHeadAndTail

fun main() {
    val numbers = readLinesFromFile("2020/day1.txt")
        .map { it.toLong() }
        .sorted()

    findNumbersSummingTo(2020, numbers, 2)?.multiply()?.also(displayPart1)
    findNumbersSummingTo(2020, numbers, 3)?.multiply()?.also(displayPart1)
}

fun findNumbersSummingTo(sumTotal: Long, optionsForDigits: List<Long>, digitsInSum: Int): List<Long>? {
    return if (digitsInSum == 1) {
        optionsForDigits.firstOrNull { it == sumTotal }?.let { listOf(it) }
    } else {
        optionsForDigits.scanHeadAndTail()
            .filter { (head) -> head < sumTotal } // just to speed it up
            .map { (head, tail) -> findNumbersSummingTo(sumTotal - head, tail, digitsInSum - 1)?.plus(head) }
            .firstOrNull { it != null }
    }
}