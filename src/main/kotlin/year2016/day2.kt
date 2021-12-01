package year2016

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.navigation.*
import utils.navigation.Coordinate.Companion.at

fun main() {
    displayPart1(squareKeypad.decode(input))
    displayPart2(starKeypad.decode(input))
}

private val input = readLinesFromFile("2016/day2.txt")
    .map { it.toCharArray().map { it.toDirection() } }

val squareKeypad = Keypad(
    at(-1, -1) to "1",
    at(0, -1) to "2",
    at(1, -1) to "3",
    at(-1, 0) to "4",
    at(0, 0) to "5",
    at(1, 0) to "6",
    at(-1, 1) to "7",
    at(0, 1) to "8",
    at(1, 1) to "9",
)

val starKeypad = Keypad(
    at(0, -2) to "1",
    at(-1, -1) to "2",
    at(0, -1) to "3",
    at(1, -1) to "4",
    at(-2, 0) to "5",
    at(-1, 0) to "6",
    at(0, 0) to "7",
    at(1, 0) to "8",
    at(2, 0) to "9",
    at(-1, 1) to "A",
    at(0, 1) to "B",
    at(1, 1) to "C",
    at(0, 2) to "D",
)

class Keypad(vararg keys: Pair<Coordinate,String>) {
    var coordinate = theOrigin
    val buttons = keys.toMap()

    fun decode(allDirections: List<List<Direction>>): String {
        coordinate = theOrigin
        return allDirections.joinToString("") { getKey(it) }
    }

    private fun move(direction: Direction) {
        coordinate = buttons.keys.firstOrNull { it == coordinate.move(direction) } ?: coordinate
    }

    private fun getKey(directions: List<Direction>): String {
        directions.forEach(::move)
        return buttons[coordinate]!!
    }
}