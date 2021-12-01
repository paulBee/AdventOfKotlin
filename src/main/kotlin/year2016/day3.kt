package year2016

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile

fun main() {
    displayPart1(inputByRow.count { it.isValid() })
    displayPart2(inputByColumn.count { it.isValid() })
}

data class Triangle(val l1: Int, val l2:Int, val l3: Int) {
    fun isValid(): Boolean = listOf(l1,l2,l3).sorted().let { it[0] + it[1] > it[2] }

    companion object {
        fun of(input: List<String>): Triangle = Triangle(input[0].toInt(), input[1].toInt(), input[2].toInt())
    }
}

private val inputByRow = readLinesFromFile("2016/day3.txt")
    .map { it.trim().split(Regex("\\s")).filter(String::isNotBlank) }
    .map { Triangle.of(it) }

private val inputByColumn = readLinesFromFile("2016/day3.txt")
    .map { it.trim().split(Regex("\\s")).filter(String::isNotBlank) }
    .let { rows -> rows.map { it[0] } + rows.map { it[1] } + rows.map { it[2] }}
    .chunked(3)
    .map { Triangle.of(it) }