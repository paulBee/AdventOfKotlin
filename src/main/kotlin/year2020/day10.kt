package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.scanHeadAndTail
import utils.collections.sumLongBy

fun main () {
    val options = readLinesFromFile("2020/day10.txt")
        .map { it.toLong() }
        .plus(0).let { it.plus(it.maxOrNull()!! + 3) }
        .sorted()

    options
        .zipWithNext()
        .map { (lower, higher) -> higher - lower }
        .groupBy { it }
        .also { collected -> displayPart1(collected[1]!!.size * (collected[3]!!.size)) }

    val mapOfTransitions = options.scanHeadAndTail()
        .map { (head, tail) -> head to tail.filter { it - head <= 3 }}
        .toMap()

    mapOfTransitions.combinationsFrom(0).also(displayPart2)

}

fun Map<Long, List<Long>>.combinationsFrom(adapter: Long, cache: MutableMap<Long, Long> = mutableMapOf()): Long =
    cache.getOrPut(adapter) {
        this[adapter]?.sumLongBy { this.combinationsFrom(it, cache) } ?: 1L
    }