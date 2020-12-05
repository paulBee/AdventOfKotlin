package year2020

import displayPart1
import displayPart2
import readLinesFromFile
import splitSize
import kotlin.math.pow

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

val rowParser = toInt('F', 'B')
val seatParser = toInt('L', 'R')

fun boardingId(row: Int, seat: Int) = row * 8 + seat

/**
 * parse a String to Int using arbitrary characters for any base
 * all chars must be provided with the first being interpreted as 0, the second 1, and so on
 */
fun toInt(vararg numberChars: Char): (String) -> Int {

    val validationRegex = numberChars.joinToString("", "^[", "]$").toRegex()
    val digitLookup = numberChars.mapIndexed { index, c -> c to index }.toMap()
    val base = numberChars.size.toDouble()

    return { string ->
        if (validationRegex.matches(string)) throw RuntimeException("That is BAD intelligence! $string has chars other than ${numberChars.joinToString(", ")}")
        string.reversed()
            .map { digitLookup[it]!! }
            .foldIndexed(0) { index, acc, next -> acc + next * base.pow(index).toInt() }
    }
}