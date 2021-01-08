package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.multiply

fun main() {
    val presents = readLinesFromFile("2015/day24.txt").map { it.toInt() }

    bestFrontCompartment(presents, 3)?.also { displayPart1(it.multiply()) }
    bestFrontCompartment(presents, 4)?.also { displayPart2(it.multiply()) }
}

private fun bestFrontCompartment(
    presents: List<Int>,
    compartments: Int
): List<Int>? {
    val compartmentSize = (presents.sum() / compartments)
    return (1..(presents.size / compartments)).asSequence()
        .map { presents.arrangementsOfSize(it, compartmentSize) }
        .first { it.isNotEmpty() }
        .sortedBy { it.multiply() }
        .firstOrNull { candidate -> presents.filterNot { candidate.contains(it) }.canMakeEqualBuckets(compartments - 1) }
}

private fun List<Int>.canMakeEqualBuckets(compartments: Int): Boolean {
    val compartmentSize = this.sum() / compartments
    return if (compartments > 2)
        bestFrontCompartment(this, compartments) != null
    else
        this.canMakeBucketOfSize(compartmentSize)
}

private fun List<Int>.canMakeBucketOfSize(size: Int): Boolean =
    this.any { it == size } || this.filter { it < size }.any { (this - it).canMakeBucketOfSize(size - it) }

private fun List<Int>.arrangementsOfSize(size: Int, total: Int): List<List<Int>> =
    when (size) {
        1 -> this.filter { it == total }.map { listOf(it) }
        else -> this
            .filter { it <= total }
            .flatMap { x -> this.filter { it != x }.arrangementsOfSize(size - 1, total - x).map { it.plus(x) }
            }
    }