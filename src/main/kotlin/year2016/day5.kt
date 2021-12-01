package year2016

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.strings.md5

fun main() {
    val hashes = generateSequence(1) { it + 1 }
        .map { "ffykfhsq$it".md5() }
        .filter { it.startsWith("00000") }
        .map { it.drop(5) }

    hashes
        .take(8)
        .joinToString("") { it.take(1) }
        .also(displayPart1)

    ('0'..'7')
        .map { position -> hashes.first { it[0] == position }}
        .map { it[1] }
        .joinToString("")
        .also(displayPart2)

}