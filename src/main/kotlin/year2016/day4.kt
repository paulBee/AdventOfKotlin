package year2016

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.strings.shift

fun main() {

    input.filter { it.isReal() }.also { displayPart1(it.sumBy { it.sector }) }
        .first() { it.decrypt().contains("northpole") }
        .also { displayPart2("${it.sector} ${it.decrypt()}") }
}


private class Room(val encryptedName: String, val sector: Int, val checksum: String) {

    fun isReal(): Boolean = sumCheck == checksum

    fun decrypt() = encryptedName.map {
        when (it) {
            '-' -> " "
            else -> it.shift(sector)
        }
    }.joinToString("")

    val sumCheck = encryptedName
        .filterNot { it == '-' }
        .groupBy { it }
        .entries.sortedWith(compareBy({ -it.value.size }, { it.key }))
        .joinToString("") { it.key.toString() }
        .take(5)

    companion object {
        val regex = Regex("""^([a-z\-]+)(\d+)\[(.*)\]$""")
        fun from(string: String): Room = regex.matchEntire(string)?.destructured?.let { (a,b,c) -> Room(a,b.toInt(),c) }!!
    }
}

private val input = readLinesFromFile("2016/day4.txt").map { Room.from(it) }