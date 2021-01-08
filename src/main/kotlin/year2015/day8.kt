package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile

fun main() {
    readLinesFromFile("2015/day8.txt")
        .sumBy { it.length - it.trimmed.length }
        .also(displayPart1)

    readLinesFromFile("2015/day8.txt")
        .sumBy { it.expanded.length - it.length }
        .also(displayPart2)
}

private val String.trimmed: String
    get() = this.drop(1).dropLast(1)
        .replace("""\\""", """\""")
        .replace("""\"""", """"""")
        .replace("""\\x[\da-f]{2}""".toRegex(), "1")

private val String.expanded: String
    get() = "\"${this
        .replace("""\""", """\\""")
        .replace(""""""", """\"""")}\""
