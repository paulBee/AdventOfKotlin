package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.scanHeadAndTail
import utils.collections.sumLongBy
import kotlin.math.absoluteValue

fun main () {
    val adapters = readLinesFromFile("2020/day10.txt").map { it.toLong() }
        .plusOutlet()
        .plusDevice()
        .sorted()

    adapters.zipWithNext()
        .groupBy { (adapter, nextAdapter) -> adapter.differenceTo(nextAdapter) }
        .let { frequencies -> frequencies.occurrencesOf(1) * frequencies.occurrencesOf(3) }
        .also(displayPart1)

    val mapOfTransitions = adapters.scanHeadAndTail()
        .map { (adapter, largerAdapters) -> adapter to largerAdapters.filter { adapter.isCompatibleWith(it) }}
        .toMap()

    mapOfTransitions.combinationsFrom(0).also(displayPart2)

}

typealias Adapter = Long

fun Adapter.differenceTo(adapter: Adapter) = (this - adapter).absoluteValue
fun Adapter.isCompatibleWith(adapter: Adapter) = this.differenceTo(adapter) <= 3

fun <T> Map<Long, List<T>>.occurrencesOf(number: Long) = this[number]?.size ?: 0

fun Map<Long, List<Long>>.combinationsFrom(adapter: Long, cache: MutableMap<Long, Long> = mutableMapOf()): Long =
    cache.getOrPut(adapter) {
        this[adapter]?.sumLongBy { this.combinationsFrom(it, cache) } ?: 1L
    }

fun List<Long>.plusOutlet() = this.plus(0)
fun List<Long>.plusDevice() = this.plus(this.maxOrNull()!! + 3)