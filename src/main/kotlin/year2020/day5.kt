package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.strings.splitAtIndex
import utils.strings.toIntUsingDigitsOf

fun main() {
    val sortedIds = readLinesFromFile("2020/day5.txt")
        .map { it.splitAtIndex(7) }
        .map { (rowStr, seatStr) -> Pair(rowParser(rowStr), seatParser(seatStr)) }
        .map { (row, seat) -> boardingId(row, seat)}
        .sorted().also { displayPart1(it.last()) }

    val seats = readLinesFromFile("2020/day5.txt")
        .map { it.fold(0) { acc, c -> acc shl 1 or (c.toInt() shr 2 xor 1 and 1) } }
        .sorted()

    sortedIds
        .zipWithNext()
        .first { (first, second) -> second - first == 2 }
        .also { displayPart2(it.first + 1)}
}

val rowParser = toIntUsingDigitsOf('F', 'B')
val seatParser = toIntUsingDigitsOf('L', 'R')

// just realised the whole thing is a binary number :man_facepalming:
// oh well the number parsing would have to take multiple characters per digit to support that which feels less useful
fun boardingId(row: Int, seat: Int) = row * 8 + seat