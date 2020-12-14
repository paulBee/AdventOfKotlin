package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.sumLong

fun main() {
    val instructions = readLinesFromFile("2020/day14.txt").map { it.toMaskMemoryInstruction() }

    memoryFrom(::OverwriteMask, instructions).values.sumLong().also(displayPart1)
    memoryFrom(::FloatingMask, instructions).values.sumLong().also(displayPart2)
}

fun <T: MemoryMask> memoryFrom(maskConstructor: (String) -> T, instructions: List<MaskMemoryInstruction>): Map<Long, Long> {
    val memory = mutableMapOf<Long, Long>()
    var mask = maskConstructor("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")

    instructions.forEach { instruction ->
            when (instruction) {
                is NewMask -> mask = maskConstructor(instruction.mask)
                is UpdateMemory ->  mask.locationsOf(instruction.address).forEach { memory[it] = mask.valueOf(instruction.value) }
            }
        }

    return memory
}

interface MemoryMask {
    fun locationsOf(location: Long) = listOf(location)
    fun valueOf(value: Long) = value
}

class OverwriteMask(val mask: String) : MemoryMask {

    override fun valueOf(value: Long) =
        mask.zip(value.to36BitString())
        .map {(mask, number) ->
            when (mask) {
                '1' -> '1'
                '0' -> '0'
                'X' -> number
                else -> throw RuntimeException("Thats a bad mask $mask")
            }
        }.joinToString("").toLong(2)

}

class FloatingMask(val mask: String): MemoryMask {

    override fun locationsOf(location: Long) =
        mask.zip(location.to36BitString()).fold(listOf(""))
        { acc, (maskEntry, numberEntry) ->
            when (maskEntry) {
                '1' -> acc.map { it.plus("1") }
                '0' -> acc.map { it.plus(numberEntry) }
                'X' -> acc.flatMap { listOf(it.plus("1"), it.plus("0")) }
                else -> throw RuntimeException("Thats a bad mask $mask")
            }
        }.map { it.toLong(2) }
}

fun Long.to36BitString() = this.toString(2).padStart(36, '0')

sealed class MaskMemoryInstruction {}
data class NewMask(val mask: String): MaskMemoryInstruction() {}
data class UpdateMemory(val address: Long, val value: Long): MaskMemoryInstruction() {}

fun String.toMaskMemoryInstruction(): MaskMemoryInstruction =
    when {
        this.startsWith("mask") -> this.toNewMask()
        this.startsWith("mem") ->  this.toUpdate()
        else -> throw RuntimeException("$this not a valid input")
    }

fun String.toNewMask() = NewMask(this.split(" = ")[1])

fun String.toUpdate() = Regex("""mem\[(\d+)\] = (\d+)""")
    .matchEntire(this)
    ?.destructured!!
    .let { (location, value) -> UpdateMemory(location.toLong(), value.toLong())}
