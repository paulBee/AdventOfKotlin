package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.sumLongBy

fun main() {
    val prezzies = readLinesFromFile("2015/day2.txt").map { it.toCuboid() }

    prezzies.sumLongBy { it.surfaceArea + it.front }.also(displayPart1)

    prezzies.sumLongBy { it.frontPerimeter + it.volume }.also(displayPart2)
}

data class Cuboid(val width: Long, val height: Long, val depth: Long) {

    val front = width * height
    val side = depth * height
    val top = width * depth

    val frontPerimeter by lazy { 2 * (width + height) }

    val surfaceArea by lazy { 2L * (front + side + top) }
    val volume by lazy { width * height * depth }
}

private fun String.toCuboid() = this.split('x').map { it.toLong() }.sorted().let { Cuboid(it[0], it[1], it[2]) }