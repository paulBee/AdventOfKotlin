package year2020

import displayPart1
import displayPart2
import readLinesFromFile
import splitSize

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
 * parse a String to Int using arbitrary characters. Up to base 10
 */
fun toInt(vararg numberChars: Char): (String) -> Int {

    if (numberChars.size > 10) throw IllegalArgumentException("String.toInt only supports up to base 10. Im not a goddam magician!")

    val validationRegex = numberChars.joinToString("", "^[", "]$").toRegex()
    val digitLookup = numberChars.mapIndexed { index, c -> c to "$index" }.toMap()

    return { string ->
        if (validationRegex.matches(string)) throw RuntimeException("That is BAD intelligence! $string has chars other than ${numberChars.joinToString(", ")}")
        string.map { digitLookup[it]!! }.joinToString("").toInt(numberChars.size)
    }
}