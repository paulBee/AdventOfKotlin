import intcodeComputers.Program
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
fun main() = runBlocking<Unit> {
    productOf((0..99), (0..99))
        .first { (noun, verb) -> configureProgram(noun, verb, programInput()).run() == 19690720L }
        .let { (noun, verb) ->
            println("Sir... we got him! \uD83D\uDC4D")
            println("noun: $noun verb: $verb answer: ${(100 * noun) + verb}")
        }
}

@ExperimentalCoroutinesApi
fun configureProgram(noun: Int, verb: Int, instruction: List<Long>): Program {
    val workingMemory = instruction.toMutableList()
    workingMemory[1] = noun.toLong()
    workingMemory[2] = verb.toLong()
    return Program(workingMemory)
}

fun programInput() : List<Long> =
    readTextFromFile("day2.txt")
        .split(",")
        .map { it.toLong() }