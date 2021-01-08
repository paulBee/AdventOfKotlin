package year2015

import isEven
import isOdd
import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile

fun main() {
    Computer(readLinesFromFile("2015/day23.txt")).also { displayPart1(it.b) }
    Computer(readLinesFromFile("2015/day23.txt"), 1).also { displayPart2(it.b) }
}

class Computer(instructions: List<String>, startAt: Int = 0) {
    val registers = mutableMapOf("a" to startAt, "b" to 0)
    val a: Int get() = registers["a"]!!
    val b: Int get() = registers["b"]!!
    var pointer = 0

    init {
        while (pointer in instructions.indices) {
            val bits = instructions[pointer].split(" ")
            when (bits[0]) {
                "hlf" -> {
                    registers[bits[1]] = registers[bits[1]]!! / 2
                    pointer ++
                }
                "tpl" -> {
                    registers[bits[1]] = registers[bits[1]]!! * 3
                    pointer ++
                }
                "inc" -> {
                    registers[bits[1]] = registers[bits[1]]!! + 1
                    pointer ++
                }
                "jmp" -> pointer += bits[1].toInt()
                "jie" -> pointer += if (registers[bits[1].dropLast(1)]!!.isEven())  bits[2].toInt() else 1
                "jio" -> pointer += if (registers[bits[1].dropLast(1)] == 1)  bits[2].toInt() else 1
            }
        }
    }
}
