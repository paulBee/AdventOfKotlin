package year2022

import utils.aoc.readTextFromFile
import utils.collections.headAndTail
import utils.collections.takeWhileInclusive

fun main() {
    val input = readTextFromFile("2022/day6.txt").toList()

    input.windowed(4)
        .takeWhileInclusive { !it.isDistinct() }
        .walled()
        .also { println(it.size) }

    input.windowed(14)
        .takeWhileInclusive { !it.isDistinct() }
        .walled()
        .also { println(it.size) }
}

fun <T> List<T>.isDistinct() = this.size == this.toSet().size

fun <T> List<List<T>>.walled(): List<T> =
    this.headAndTail().let { (head, tail) -> head + tail.map { it.last() } }
