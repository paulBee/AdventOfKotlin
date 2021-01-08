package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.strings.isNumber

fun main() {
    val wireToInput = readLinesFromFile("2015/day7.txt")
        .map { it.split(" -> ") }
        .map { (input, name) -> name to input }
        .toMap().toMutableMap()

    val originalA = wireToInput.valueOfWire("a").also(displayPart1)

    wireToInput["b"] = originalA.toString()

    wireToInput.valueOfWire("a").also(displayPart2)


}

private fun Map<String,String>.valueOfWire(wireName: String, cache: MutableMap<String, Int> = mutableMapOf()): Int =
    cache.getOrPut(wireName) {
        if (wireName.isNumber())
            wireName.toInt()
        else {
            val input = this[wireName]!!
            when {
                input.contains("AND") -> input.split(" AND ")
                    .let { (a, b) -> this.valueOfWire(a, cache) and this.valueOfWire(b, cache) }
                input.contains("OR") -> input.split(" OR ").let { (a, b) -> this.valueOfWire(a, cache) or this.valueOfWire(b, cache) }
                input.contains("LSHIFT") -> input.split(" LSHIFT ")
                    .let { (a, b) -> this.valueOfWire(a, cache).shl(this.valueOfWire(b, cache)) }
                input.contains("RSHIFT") -> input.split(" RSHIFT ")
                    .let { (a, b) -> this.valueOfWire(a, cache).shr(this.valueOfWire(b, cache)) }
                input.contains("NOT") -> input.drop(4).let { 65535 - this.valueOfWire(it, cache) }
                else -> this.valueOfWire(input, cache)
            }
        }
    }