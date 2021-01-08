package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.navigation.Coordinate

fun main() {
    val init = readLinesFromFile("2015/day18.txt")
        .flatMapIndexed { y, row -> row.toCharArray().mapIndexed() { x, char -> Coordinate(x,y) to char } }
        .toMap()

    generateSequence(init) { it.next() }.elementAt(100)
        .values.count { it == '#' }.also(displayPart1)

    generateSequence(init) { it.nextFixedCorners() }.elementAt(100)
        .values.count { it == '#' }.also(displayPart2)

}

private fun Map<Coordinate, Char>.next(): Map<Coordinate, Char> =
    this.mapValues { (coord, isOn) ->
        when ((coord.orthognals + coord.diagonals).count { this[it] == '#' }) {
            3 -> true
            2 -> isOn == '#'
            else -> false
        }.let { if (it) '#' else '.'}
    }

private fun Map<Coordinate, Char>.nextFixedCorners(): Map<Coordinate, Char> {

    val xs = this.keys.map { it.x }
    val ys = this.keys.map { it.y }

    val corners = listOf(
        Coordinate(xs.minOrNull()!!, ys.minOrNull()!!),
        Coordinate(xs.minOrNull()!!, ys.maxOrNull()!!),
        Coordinate(xs.maxOrNull()!!, ys.minOrNull()!!),
        Coordinate(xs.maxOrNull()!!, ys.maxOrNull()!!),
    )

    return this.mapValues { (coord, isOn) ->
        (coord in corners ||
        when ((coord.orthognals + coord.diagonals).count { it in corners || this[it] == '#' }) {
            3 -> true
            2 -> isOn == '#'
            else -> false
        }).let { if (it) '#' else '.'}
    }
}