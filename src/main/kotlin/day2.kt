import intcodeComputers.Computer

fun main() {
    val computer = Computer(programInput())

    productOf((0..99), (0..99))
        .first { (noun, verb) -> computer.runProgram(noun, verb) == 19690720 }
        .let { (noun, verb) ->
            println("Sir... we got him! \uD83D\uDC4D")
            println("noun: $noun verb: $verb answer: ${(100 * noun) + verb}")
        }
}

fun programInput() : List<Int> =
    readTextFromFile("day2.txt")
        .split(",")
        .map { it.toInt() }