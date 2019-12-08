import intcodeComputers.Program

fun main() {
    productOf((0..99), (0..99))
        .first { (noun, verb) -> configureProgram(noun, verb, programInput()).run() == 19690720 }
        .let { (noun, verb) ->
            println("Sir... we got him! \uD83D\uDC4D")
            println("noun: $noun verb: $verb answer: ${(100 * noun) + verb}")
        }
}

fun configureProgram(noun: Int, verb: Int, instruction: List<Int>): Program {
    val workingMemory = instruction.toMutableList()
    workingMemory[1] = noun
    workingMemory[2] = verb
    return Program(workingMemory)
}

fun programInput() : List<Int> =
    readTextFromFile("day2.txt")
        .split(",")
        .map { it.toInt() }