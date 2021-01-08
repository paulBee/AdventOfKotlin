package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.strings.splitAtIndex

fun main() {
    val results = mapOf(
    "children" to 3,
    "cats" to 7,
    "samoyeds" to 2,
    "pomeranians" to 3,
    "akitas" to 0,
    "vizslas" to 0,
    "goldfish" to 5,
    "trees" to 3,
    "cars" to 2,
    "perfumes" to 1
    )

    val sues = readLinesFromFile("2015/day16.txt").map { Sue.from(it) }

    sues
        .first { sue -> sue.knownProperties.all { prop -> results[prop.first] == prop.second } }
        .also { displayPart1(it.number) }

    sues
        .first { sue -> sue.knownProperties.all { (key, value) ->
            when (key) {
                "cats" -> results[key]!! < value
                "trees" -> results[key]!! < value
                "pomeranians" -> results[key]!! > value
                "goldfish" -> results[key]!! > value
                else -> results[key] == value
            }
        } }
        .also { displayPart2(it.number) }
}

data class Sue(val number: String, val knownProperties: List<Pair<String, Int>>) {

    companion object {
        fun from(input:String):Sue {
            val (number, props) = input.splitAtIndex(input.indexOfFirst { it == ':' })

            return Sue(
                number.drop(4),
                props.drop(2)
                    .split(", ")
                    .map { it.split(": ") }
                    .map { it[0] to it[1].toInt() }
            )
        }
    }
}

