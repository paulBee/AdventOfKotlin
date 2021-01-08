package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readTextFromFile

fun main() {
    val text = readTextFromFile("2015/day12.txt")
    val json = JsonParser(text).takeValue()
    displayPart1(sumNumbers(json))
    displayPart2(sumNumbers(json, includeRed = false))
}

fun sumNumbers(json: Any?, includeRed: Boolean = true): Int =
    when (json) {
        is Map<*,*> -> if (!json.containsValue("red") || includeRed) json.values.sumBy { sumNumbers(it, includeRed) } else 0
        is List<*> -> json.sumBy { sumNumbers(it, includeRed) }
        is Int -> json
        else -> 0
    }

class JsonParser(val string: String) {
    val input = string.filter { it != ' ' }
    var currentIndex = 0

    fun takeValue(): Any =
        when (currentChar()) {
            '[' -> takeList()
            '{' -> takeMap()
            '"' -> takeString()
            else -> takeInt()
        }

    fun takeString(): String {
        val stringStart = ++currentIndex
        while (currentChar() != '"') currentIndex++
        return input.take(currentIndex).drop(stringStart).also { currentIndex++ }
    }

    fun takeInt(): Int {
        val intStart = currentIndex
        while (currentChar() in ('0'..'9').plus('-')) currentIndex++
        return input.take(currentIndex).drop(intStart).toInt()
    }

    fun takeMap(): Map<String,Any> {
        val map = mutableMapOf<String,Any>()
        currentIndex++
        while (currentChar() != '}') {
            val key = takeString()
            skip(':')
            val value = takeValue()
            map[key] = value
            skip(',')
        }
        currentIndex++
        return map
    }

    fun takeList(): List<Any> {
        val list = mutableListOf<Any>()
        currentIndex++
        while (currentChar() != ']') {
            list.add(takeValue())
            skip(',')
        }
        currentIndex++
        return list
    }

    fun currentChar(): Char? = input.getOrNull(currentIndex)
    fun skip(char: Char) {
        while (currentChar() == char) currentIndex++
    }
}