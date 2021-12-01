package year2016

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readTextFromFile

fun main() {
    val compressed = readTextFromFile("2016/day9.txt")

    decompressedLength(compressed, false).also { displayPart1(it) }
    decompressedLength(compressed, true).also { displayPart2(it) }
}

fun decompressedLength(compressed: String, deep: Boolean): Long {
    var index = 0
    var outputLength = 0L

    fun takeNumber(): Int {
        var number = ""
        while (compressed[index].isDigit()) {
            number += compressed[index]
            index++
        }
        return number.toInt()
    }

    fun takeCompressionInstr(): Pair<Int, Int> {
        index++
        val repeat = takeNumber()
        index++
        val times = takeNumber()
        index++
        return repeat to times
    }

    fun takeLength(length: Int): Long {
        var string = ""
        repeat(length) {
            string += compressed[index]
            index++
        }
        return if (deep) decompressedLength(string, deep) else string.length.toLong()
    }

    while (index < compressed.length) {
        val currentChar = compressed[index]
        if (currentChar == '(') {
            val (length, times) = takeCompressionInstr()
            val toRepeat: Long = takeLength(length)

            outputLength += toRepeat * times
        } else {
            outputLength ++
            index++
        }
    }

    return outputLength
}