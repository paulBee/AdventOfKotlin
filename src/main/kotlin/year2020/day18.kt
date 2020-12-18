package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.multiply
import utils.collections.sumLongBy

fun main() {
    readLinesFromFile("2020/day18.txt").sumLongBy { parse1(it) }.also(displayPart1)
    readLinesFromFile("2020/day18.txt").sumLongBy { parse2(it) }.also(displayPart2)
}

val parse1 = parseWithPrecedence(::fromLeft)
val parse2 = parseWithPrecedence(::additionFirst)

fun parseWithPrecedence(then: (String) -> Long): (String) -> Long =
    { string: String ->
        if (string.contains('(')) {
            val innerExpression = string.takeWhile { it != ')' }.takeLastWhile { it != '(' }
            val simplerString = string.replace("($innerExpression)", then(innerExpression).toString())
            parseWithPrecedence(then)(simplerString)
        } else {
            then(string)
        }
    }

fun additionFirst(string: String) = string.split(" * ").map { fromLeft(it) }.multiply()

fun fromLeft(string: String): Long {
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
