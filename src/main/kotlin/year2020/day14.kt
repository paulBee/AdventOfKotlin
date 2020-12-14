package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.sumLongBy

fun main() {
    part1()

    val memory = mutableMapOf<BooleanNumber, Int>()
    var mask = BitMask()
    readLinesFromFile("2020/day14.txt").forEach { line ->
        when {
            line.startsWith("mask") -> mask = line.toMask()
            line.startsWith("mem") -> line.toLocationAndValue().also {
                    (location, number) -> mask.applyFloating(location.toBinaryNumber()).forEach { memory[it] = number.toInt() }
            }
        }
    }

    memory.values.sumLongBy { it.toLong() }.also(displayPart2)

}

private fun part1() {
    val memory = mutableMapOf<Int, BooleanNumber>()
    var mask = BitMask()
    readLinesFromFile("2020/day14.txt").forEach { line ->
        when {
            line.startsWith("mask") -> mask = line.toMask()
            line.startsWith("mem") ->  line.toLocationAndValue().also {
                (location, number) -> memory[location.toInt()] = mask.applyOverwrite(number.toBinaryNumber())
            }
        }
    }
    memory.values.sumLongBy { it.toLong() }.also(displayPart1)
}

typealias BooleanNumber = List<Boolean>

private fun BooleanNumber.toLong() =
    this.map {
        when (it) {
            true -> '1'
            false -> '0'
        }
    }.joinToString("").toLong(2)

fun String.toMask() = BitMask(this.split(" = ")[1])

fun String.toLocationAndValue() = Regex("""mem\[(\d+)\] = (\d+)""")
    .matchEntire(this)
    ?.destructured!!
    .let { (location, value) -> location to value}

fun String.toBinaryNumber() = this.toInt()
    .toString(2)
    .padStart(36, '0')
    .map { when(it) {
        '1' -> true
        '0' -> false
        else -> throw RuntimeException("$it is not a binary digit")
    } }


class BitMask(inputString: String = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX") {
    val mask: List<Boolean?> = inputString.map {
        when (it) {
            'X' -> null
            '1' -> true
            '0' -> false
            else -> throw RuntimeException("$it is an invalid bitmask character")
        }
    }

    fun applyOverwrite(binaryNumber: BooleanNumber): BooleanNumber =
        mask.zip(binaryNumber)
            .map {(mask, number) -> mask ?: number }

    fun applyFloating(binaryNumber: BooleanNumber): List<BooleanNumber> =
        mask.zip(binaryNumber).fold(listOf(listOf()))
        { acc, (maskEntry, numberEntry) ->
            when (maskEntry) {
                true -> acc.map { it.plus(true) }
                false -> acc.map { it.plus(numberEntry) }
                else -> acc.flatMap { listOf(it.plus(true), it.plus(false)) }
            }
        }
}