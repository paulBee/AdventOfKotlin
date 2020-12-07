package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.keysWhereValue
import utils.collections.repeated

fun main() {
    val bagRuler = BagHelper(readLinesFromFile("2020/day7.txt"))

    bagRuler.bagsThatCanContain("shiny gold").also { displayPart1(it.size) }
    bagRuler.bagsInsideTheBag("shiny gold").also { displayPart2(it.size) }
}

class BagHelper(inputs: List<String>) {

    private val parsedInput: Map<String, List<NameAndNumber>> = inputs.map { parse(it) }.toMap()

    fun bagsThatCanContain(bagName: String): Set<String> =
        parsedInput.keysWhereValue { it.any { (name) -> name == bagName }}
            .flatMap { bagsThatCanContain(it).plus(it) }
            .toSet()

    fun bagsInsideTheBag(bagName: String): List<String> =
        parsedInput.getOrDefault(bagName, emptyList())
            .flatMap { (bagsInsideTheBag(it.name).plus(it.name)).repeated(it.number) }
}

data class NameAndNumber(val name: String, val number: Int)

val numberAndName = """(\d+) ([a-z]+ [a-z]+) bag.*""".toRegex()
fun parse(input: String) =
    input.split(" bags contain ")
        .let { (name, rest) ->
            name to
            listOf(rest)
                .filter { it != "no other bags." }
                .flatMap { it.split(", ") }
                .map { numberAndName.matchEntire(it)?.destructured ?: throw RuntimeException(it) }
                .map {(number, name) -> NameAndNumber(name, number.toInt())}
        }