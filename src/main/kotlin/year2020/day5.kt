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

val rowParser = binaryParser('F', 'B')
val seatParser = binaryParser('L', 'R')

fun boardingId(row: Int, seat: Int) = row * 8 + seat

fun binaryParser(zeroChar: Char, oneChar: Char): (String) -> Int {
    val validationRegex = Regex("^[$zeroChar$oneChar]+$")
    return {
        if (validationRegex.matches(it))
            it.replace(oneChar, '1').replace(zeroChar, '0').toInt(2)
        else throw RuntimeException("That is BAD intelligence! $it has chars other than $zeroChar and $oneChar")
    }

}