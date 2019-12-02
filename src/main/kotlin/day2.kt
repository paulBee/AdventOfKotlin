import intcodeComputers.Computer

fun main() {
    val computer = Computer(programInput())

    (0..99).forEach { noun -> (0..99).forEach { verb ->
        if (computer.runProgram(noun, verb) == 19690720) {
            println("Sir... we got him! \uD83D\uDC4D")
            println("noun: $noun verb: $verb answer: ${(100 * noun) + verb}")
        }
    } }
}

fun programInput() : List<Int> =
    readTextFromFile("day2.txt")
        .split(",")
        .map { it.toInt() }