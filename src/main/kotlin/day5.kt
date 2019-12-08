import intcodeComputers.Program

fun main() {
    val instructions = readTextFromFile("day5.txt").split(",").map { it.toInt() }

    println(Program(instructions.toMutableList(),5 ).run())


}