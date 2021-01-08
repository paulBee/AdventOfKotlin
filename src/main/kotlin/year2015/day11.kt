package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2

fun main() {
    generateSequence("hepxcrrq") { it.increment() }.drop(1)
        .filter { it.isValid() }
        .take(2)
        .also { displayPart1(it.first()) }
        .also { displayPart2(it.last()) }
}

val noOIL = Regex("^[^iol]+$")

private fun String.isValid(): Boolean =
    this.containsIncreasingStraight() &&
            noOIL.matches(this) &&
            this.hasDoubles(2)

private fun String.hasDoubles(x: Int): Boolean =
    when {
        this.length < 2 -> false
        this[0] != this[1] -> this.drop(1).hasDoubles(x)
        x > 1 -> this.drop(2).hasDoubles(x - 1)
        else -> true
    }

private fun String.containsIncreasingStraight(): Boolean =
    this.toCharArray().asSequence().windowed(3).any { (a,b,c) -> a == b - 1 && a == c - 2}

private tailrec fun String.increment(index: Int = this.length - 1): String {
    return when {
        index < 0 -> "a$this"
        this[index] != 'z' -> (this.take(index) + (this[index] + 1) + this.drop(index + 1))
        else -> (this.take(index) + 'a' + this.drop(index + 1)).increment(index - 1)
    }
}