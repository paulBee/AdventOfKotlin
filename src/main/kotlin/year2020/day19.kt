package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile

fun main() {

    val messages = readLinesFromFile("2020/day19.txt").takeLastWhile { it.isNotBlank() }

    val rules1 = readLinesFromFile("2020/day19.txt").takeWhile { it.isNotBlank() }
        .map { it.toNameAndExpansion() }
        .toMap()

    val rules2 = readLinesFromFile("2020/day19.txt").takeWhile { it.isNotBlank() }
        .map {
            when {
                it.startsWith("8:") -> "8: 42 | 42 8"
                it.startsWith("11:") -> "11: 42 31 | 42 11 31"
                else -> it
            }
        }
        .map { it.toNameAndExpansion() }
        .toMap()

    messages.count { isMatch(rules1, it, listOf("0")) }.also(displayPart1)
    messages.count { isMatch(rules2, it, listOf("0")) }.also(displayPart2)


}

private fun isMatch(ruleExpansions: Map<String, ExpansionRule>, message: CharSequence, expansions: List<String>): Boolean =
    when {
        message.isEmpty() && expansions.isEmpty() -> true
        message.isEmpty() xor expansions.isEmpty() -> false
        else -> {
            val firstExpansion = ruleExpansions.getValue(expansions.first())
            when {
                firstExpansion.isTerminal() -> message.first() == firstExpansion.first() && // most messages fail here. would be nice to make that more obvious
                        isMatch(ruleExpansions, message.drop(1), expansions.drop(1))
                else -> firstExpansion.split(" | ")
                    .map { it.split(" ") + expansions.drop(1) }
                    .any { isMatch(ruleExpansions, message, it) }
            }
        }
    }

typealias ExpansionRule =  String

fun ExpansionRule.isTerminal() = this.first().isLetter()

fun String.toNameAndExpansion() = this.split(": ")
    .let { (name, expansion) -> name to (expansion.replace("\"", "")) }
