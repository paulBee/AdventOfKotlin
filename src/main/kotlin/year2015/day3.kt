package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readTextFromFile
import utils.navigation.*

fun main() {
    val input = readTextFromFile("2015/day3.txt").asSequence()

    displayPart1(input.trackLocations().toSet().size)

    input.asSequence().alternating()
        .let { (santa, roboSanta) -> santa.trackLocations().toSet() union roboSanta.trackLocations().toSet() }
        .also { displayPart2(it.size) }
}

private fun <T> Sequence<T>.alternating(): Pair<Sequence<T>, Sequence<T>> {
    return Pair (
        this.filterIndexed { i, _ -> i % 2 == 0 },
        this.filterIndexed { i, _ -> i % 2 == 1 }
    )

}

private fun Sequence<Char>.trackLocations() =
    this.scan(Coordinate(0,0))
    { last, next -> when (next) {
        '^' -> last.move(Up)
        '>' -> last.move(Right)
        'v' -> last.move(Down)
        '<' -> last.move(Left)
        else -> throw RuntimeException("turn around and touch the ground")
    } }
