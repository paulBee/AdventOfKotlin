package year2020

import displayPart1
import displayPart2
import readLinesFromFile
import splitAtIndex
import toIntUsingDigitsOf

fun main() {
    val sortedIds = readLinesFromFile("2020/day5.txt")
        .map { it.splitAtIndex(7) }
        .map { (rowStr, seatStr) -> Pair(rowParser(rowStr), seatParser(seatStr)) }
        .map { (row, seat) -> boardingId(row, seat)}
        .sorted().also { displayPart1(it.last()) }

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