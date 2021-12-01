package year2016

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readTextFromFile
import utils.collections.firstDuplicate
import utils.navigation.*

fun main() {
    val initial: Pair<Direction, List<Coordinate>> = Up to listOf(theOrigin)

    readTextFromFile("2016/day1.txt")
        .split(", ")
        .map { it.take(1).toLorR() to it.drop(1).toInt()}
        .fold(initial) { (direction, coordinates), (turn, distance) -> direction.rotate(turn).let { newDirection ->
                newDirection to coordinates + generateSequence(coordinates.last()) { it.move(newDirection) }.drop(1).take(distance)}
        }
        .second
        .also { displayPart1(it.last().manhattanDisplacement()) }
        .firstDuplicate()?.also { displayPart2(it.manhattanDisplacement()) }
}

private fun String.toLorR(): Rotation = if (this == "R") Right else Left
