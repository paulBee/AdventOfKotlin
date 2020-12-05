package year2020

import displayPart1
import displayPart2
import readLinesFromFile
import splitSize
import toIntUsingDigitsOf

fun main() {
    val sortedIds = readLinesFromFile("2020/day5.txt")
        .map { it.splitSize(7) }
        .map{ (rowStr, seatStr) -> Pair(rowParser(rowStr), seatParser(seatStr)) }
        .map{ (row, seat) -> boardingId(row, seat)}
        .sorted()

    val min = sortedIds.first()
    val max = sortedIds.last().also(displayPart1)

    val firstMissingNumber = (min..max zip sortedIds).first { (x, y) -> x != y }.first

    displayPart2(firstMissingNumber)
}

val rowParser = toIntUsingDigitsOf('F', 'B')
val seatParser = toIntUsingDigitsOf('L', 'R')

fun boardingId(row: Int, seat: Int) = row * 8 + seat