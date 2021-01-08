package year2015

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentSet
import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile

fun main() {

    val containers = readLinesFromFile("2015/day17.txt").map { Container(it.toInt()) }.toSet()

    containers.countWaysThanSumTo(150).also { displayPart1(it.size) }
        .groupBy { it.size }
        .entries.minByOrNull { it.key }
        ?.also { displayPart2(it.value.size) }
}

private fun Set<Container>.countWaysThanSumTo(
    sum: Int,
    summingWith: PersistentSet<Container> = emptySet<Container>().toPersistentSet()
): Set<Set<Container>> =
    this.flatMap {
        when {
            it.volume in 0 until sum -> (this - it)
                .countWaysThanSumTo(sum - it.volume, summingWith.add(it))
            it.volume == sum -> setOf(summingWith + it)
            else -> emptySet()
        }
    }.toSet()


private class Container(val volume: Int) {
    override fun toString() = volume.toString()
}