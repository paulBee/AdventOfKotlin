package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.sumLongBy

fun main() {
    readLinesFromFile("2020/day18.txt").sumLongBy { parse1(it) }.also(displayPart1)
    readLinesFromFile("2020/day18.txt").sumLongBy { parse2(it) }.also(displayPart2)
}

val parse1: (string: String) -> Long = expandBrackets(::operateFromLeft)
val parse2: (string: String) -> Long = expandBrackets(expandAddition(::operateFromLeft))

fun expandBrackets(then: (String) -> Long): (String) -> Long {

    fun recurse(string: String): Long =
        if (string.contains('(')) {

            val indexOfLastBracket = string.indexOfFirst { it == ')' }
            val startOfString = string.take(indexOfLastBracket + 1)

            val indexOfStartBracket = startOfString.indexOfLast { it == '(' }
            val innerBrackets =  startOfString.drop(indexOfStartBracket)
            val simplerString = string.replace(innerBrackets, then(innerBrackets.drop(1).dropLast(1)).toString())

            recurse(simplerString)
        } else {
            then(string)
        }

    return ::recurse
}

fun expandAddition(then: (String) -> Long): (String) -> Long {

    fun recurse(string: String): Long =
        if (string.contains('+')) {

            val indexOfPlus = string.indexOfFirst { it == '+' }
            val firstNumber = string.take(indexOfPlus - 1).takeLastWhile { it.isDigit() }
            val secondNumber = string.drop(indexOfPlus + 2).takeWhile { it.isDigit() }

            val simplerString = string.take(indexOfPlus - (1 + firstNumber.length)) +
                    then("$firstNumber + $secondNumber").toString() +
                    string.drop(indexOfPlus + 2 + secondNumber.length)

            recurse(simplerString)
        } else {
            then(string)
        }

    return ::recurse
}

fun operateFromLeft(string: String): Long {
    val bits = string.split(" ")
    val head = bits.first()
    val tail = bits.drop(1).chunked(2)

    return tail.fold(head.toLong()) { acc, (operator, number) ->
        when (operator) {
            "+" -> acc + number.toLong()
            "*" -> acc * number.toLong()
            else -> throw RuntimeException("unsupported operator: $string")
        } }
}
