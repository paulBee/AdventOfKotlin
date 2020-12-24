package year2020

import isOdd
import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.chunkWhen
import utils.collections.keysWhereValue
import utils.collections.productOf
import utils.hof.firstArg
import utils.hof.oneOf

fun main() {
    val directions = readLinesFromFile("2020/day24.txt")

    val initial = directions.groupBy { it.tokenizeToHex().reduce { acc, next -> acc + next } }
        .keysWhereValue { it.size.isOdd() }
        .toSet().also { displayPart1(it.size) }

    generateSequence(initial, ::iterate).elementAt(100).also { displayPart2(it.size) }
}

private fun iterate(last: Set<HexVector>): Set<HexVector> =
    last.expand().filter {
        when (it.neighbours().count { neighbour -> last.contains(neighbour) }) {
            1 -> last.contains(it)
            2 -> true
            else -> false
        }
    }.toSet()

private fun Set<HexVector>.expand() :Set<HexVector> = this.flatMap { listOf(it) + it.neighbours() }.toSet()

private fun String.tokenizeToHex() = this.toList()
    .chunkWhen(firstArg(oneOf('e','w')))
    .map { it.joinToString("") }
    .map(HexVector::of)

data class HexVector(val east: Int, val northEast: Int) {

    operator fun plus(other: HexVector) = HexVector(east + other.east, northEast + other.northEast)

    fun neighbours(): List<HexVector> = productOf((-1..1), (-1..1))
        .filter { it.first != it.second }
        .map { HexVector(east + it.first, northEast + it.second) }

    companion object {
        fun of(it: String) = when(it) {
            "e" -> HexVector(1,0)
            "w" -> HexVector(-1,0)
            "ne" -> HexVector(0,1)
            "sw" -> HexVector(0,-1)
            "nw" -> HexVector(-1,1)
            "se" -> HexVector(1,-1)
            else -> throw RuntimeException("$it is not a hex direction")
        }
    }
}