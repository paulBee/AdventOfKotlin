package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.windowedWithTail
import utils.hof.oneOf
import utils.strings.isVowel


fun main() {
    readLinesFromFile("2015/day5.txt").count { it.has3vowels() && it.hasDuplicate() && it.noBaddies() }.also(displayPart1)
    readLinesFromFile("2015/day5.txt").count { it.hasSameBreadSandwich() && it.hasDoubleDouble() }.also(displayPart2)
}


private fun String.has3vowels() = this.count { it.isVowel() } >= 3
private fun String.hasDuplicate() = this.zipWithNext().any { (a, b) -> a == b }
private fun String.noBaddies() = this.windowed(2).none { oneOf("ab", "cd", "pq", "xy")(it) }
private fun String.hasSameBreadSandwich() = this.windowed(3).any { it.first() == it.last() }
private fun String.hasDoubleDouble() = this.toList().windowedWithTail(2)
    .any { (it, tail) -> tail.windowed(2).any { tailPair -> tailPair == it } }