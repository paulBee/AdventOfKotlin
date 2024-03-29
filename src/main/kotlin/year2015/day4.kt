package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.collections.takeWhileInclusive
import utils.strings.md5
import java.math.BigInteger
import java.security.MessageDigest

fun main() {
    val hashes = sequence.map { it.md5() }
    hashes.takeWhileInclusive { !it.startsWith("00000") }.also { displayPart1(it.count()) }
    hashes.takeWhileInclusive { !it.startsWith("000000") }.also { displayPart2(it.count()) }
}

val sequence = generateSequence(1) { it + 1 }.map { "yzbqklnj$it" }