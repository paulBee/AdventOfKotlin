package year2020

import utils.aoc.displayPart1
import utils.collections.takeWhileInclusive

fun main() {
    val publicKey1 = 335121L
    val publicKey2 = 363891L

    val subjectNumber = 7L

    val loop1 = sequence(subjectNumber).takeWhileInclusive { it != publicKey1 }.count()
    val loop2 = sequence(subjectNumber).takeWhileInclusive { it != publicKey2 }.count()

    sequence(publicKey1).take(loop2).last().also(displayPart1)
    sequence(publicKey2).take(loop1).last().also(displayPart1)
}

private fun sequence(subjectNumber: Long) = generateSequence(1L) { (it * subjectNumber) % 20201227L }

