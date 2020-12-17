package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.multiply

fun main() {
    val input = readLinesFromFile("2020/day16.txt")
    val fields = input.parseFields()
    val myTicket = input.parseMyTicket()
    val nearbyTickets = input.parseNearbyTickets()

    nearbyTickets
        .flatMap { it.values }
        .filter { number -> fields.none { number validFor it } }
        .sum()
        .also(displayPart1)

    val validTickets = nearbyTickets.filter { it.values.none { number -> fields.none { field -> number validFor field } } }

    val fieldToPossibleIndices = fields.map { it to it.indiciesAllPass(validTickets) }
    fieldToIndex(fieldToPossibleIndices)
        .filter { it.first.name.startsWith("departure") }
        .map { it.second }
        .map { myTicket.values[it] }
        .multiply().also(displayPart2)

}

fun fieldToIndex(fieldToPossibleIndices: List<Pair<Field, List<Int>>>): List<Pair<Field, Int>> {

    if (fieldToPossibleIndices.size == 0) return emptyList()

    val (singleOption, manyOptions) = fieldToPossibleIndices.partition { it.second.size == 1 }

    val identifiedIndices = singleOption.map { it.second.first() }
    val filteredRemaining = manyOptions.map { it.first to it.second.filter { !identifiedIndices.contains(it) } }


    return singleOption.map { it.first to it.second.first() }.plus(fieldToIndex(filteredRemaining))
}

data class Range(val min: Int, val max: Int) {}

data class Field(val name: String, val firstRange: Range, val secondRange: Range) {

    fun indiciesAllPass(tickets: List<Ticket>) =
        tickets.first().values.indices
            .filter { index -> tickets.all { ticket -> ticket.values[index] validFor this } }

    override fun toString() = name
}

data class Ticket(val values: List<Int>)
infix fun Int.within(range: Range) = this in range.min..range.max
infix fun Int.validFor(field: Field) = this within field.firstRange || this within field.secondRange

val fieldRegex = """(.*): (\d+)-(\d+) or (\d+)-(\d+)""".toRegex()
fun List<String>.parseFields() =
    this.takeWhile { it.isNotBlank() }
        .map { fieldRegex.matchEntire(it)?.destructured ?: throw RuntimeException("invalid field: $it")}
        .map { (name, firstMin, firstMax, secondMin, secondMax) ->
            Field(name, Range(firstMin.toInt(), firstMax.toInt()), Range(secondMin.toInt(), secondMax.toInt())) }

fun List<String>.parseMyTicket() =
    this.dropWhile { it != "your ticket:"}.drop(1).first()
        .let { line -> Ticket(line.split(",").map { it.toInt() }) }


fun List<String>.parseNearbyTickets() =
    this.dropWhile { it != "nearby tickets:"}.drop(1).toList()
        .map { line -> Ticket(line.split(",").map { it.toInt() }) }

