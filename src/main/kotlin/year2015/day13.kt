package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.arrangements

fun main() {
    val personInfo = readLinesFromFile("2015/day13.txt").map { it.parse() }.groupBy { it.person }

    personInfo.keys.arrangements.map { seating -> tableHappiness(seating, personInfo) }.also { displayPart1(it.max()!!) }
    (personInfo.keys + "ME!").arrangements.map { seating -> tableHappiness(seating, personInfo) }.also { displayPart2(it.max()!!) }
}

private fun tableHappiness(
    seating: List<String>,
    personInfo: Map<String, List<Parsed>>
) = (seating.takeLast(1) + seating + seating.take(1))
    .windowed(3)
    .sumBy { (left, person, right) ->
        ((personInfo[person]?.firstOrNull { it.neighbour == left }?.happiness) ?: 0) +
        (personInfo[person]?.firstOrNull { it.neighbour == right }?.happiness ?: 0)
    }

val inputRegex = Regex("(\\w+) would (gain|lose) (\\d+) happiness units by sitting next to (\\w+).")
private fun String.parse(): Parsed {
    val (person, gainLose, amount, neighbour) = inputRegex.matchEntire(this)?.destructured ?: throw RuntimeException("failed parse: $this")
    return Parsed(person, neighbour, if (gainLose == "gain") amount.toInt() else 0 - amount.toInt())
}

private data class Parsed(val person: String, val neighbour: String, val happiness: Int)
