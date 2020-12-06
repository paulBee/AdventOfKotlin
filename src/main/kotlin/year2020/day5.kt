package year2020

import pow
import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.pickRandom
import utils.aoc.readLinesFromFile
import utils.collections.bifurcate
import utils.strings.splitAtIndex
import utils.strings.toIntUsingDigitsOf

fun main() {
    val input = readLinesFromFile("2020/day5.txt")

    val sortedIds = input
        .map {
            pickRandom(
                ::parseTheParcel,
                ::aShiftySolution,
                ::hideAndSeek
            )(it)
        }
        .sorted().also { displayPart1(it.last()) }

    sortedIds
        .zipWithNext()
        .first { (first, second) -> second - first == 2 }
        .also { displayPart2(it.first + 1)}
}



fun aShiftySolution(boarding: String) =
    boarding.fold(0)
        { acc, c ->
            (acc shl 1).let {
                when(c) {
                    'B' -> it + 1
                    'R' -> it + 1
                    else -> it
                }
            }
        }



fun parseTheParcel(boarding: String) = boarding.splitAtIndex(7)
    .let {
        (row, seat) -> boardingId(rowParser(row), seatParser(seat))
    }

fun boardingId(row: Int, seat: Int) = row * 8 + seat

val rowParser = toIntUsingDigitsOf('F', 'B')
val seatParser = toIntUsingDigitsOf('L', 'R')



fun hideAndSeek (boarding: String) = boarding.splitAtIndex(7)
    .let {
        (row, seat) -> boardingId(
            followDirectionsToNumber(row, 'F', 'B'),
            followDirectionsToNumber(seat, 'L', 'R')
        )
    }

fun followDirectionsToNumber (directions: String, lower: Char, upper: Char) =
    directions.fold(
        (0 until (2 pow directions.length)).toList())
        { acc, next ->
            when (next) {
                upper -> acc.bifurcate().second
                lower -> acc.bifurcate().first
                else -> throw RuntimeException("I'm sorry, I cant do that Dave")
            }
        }
        .let {
            assert(it.size == 1)
            it[0]
        }
