import intcodeComputers.Program
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
fun main() = runBlocking<Unit> {
    val programInstructions = readTextFromFile("day7.txt").split(",").map { it.toInt() }

    val max = generateCombinationsOf(listOf(0, 1, 2, 3, 4))
        .map {
            val guess = runPhaseGuess(programInstructions, it)
            println("$it = $guess")
            guess

        }
        .max()

    println(max)

//    runPhaseGuess(programInstructions, )


}

@ExperimentalCoroutinesApi
private suspend fun runPhaseGuess(programInstructions: List<Int>, phaseGuess: List<Int>): Int {
    val (phase1, phase2, phase3, phase4, phase5) = phaseGuess
    val firstInput = Channel<Int>(Channel.UNLIMITED)
    val firstOutput = Channel<Int>(Channel.UNLIMITED)
    val secondOutput = Channel<Int>(Channel.UNLIMITED)
    val thirdOutput = Channel<Int>(Channel.UNLIMITED)
    val fourthOutput = Channel<Int>(Channel.UNLIMITED)
    val fifthOutput = Channel<Int>(Channel.UNLIMITED)
    firstInput.send(phase1)
    firstOutput.send(phase2)
    secondOutput.send(phase3)
    thirdOutput.send(phase4)
    fourthOutput.send(phase5)
    firstInput.send(0)
    GlobalScope.launch {
        Program(programInstructions.toMutableList(), firstInput, firstOutput, "prog1").run()
    }
    GlobalScope.launch {
        Program(programInstructions.toMutableList(), firstOutput, secondOutput, "prog2").run()
    }

    GlobalScope.launch {
        Program(programInstructions.toMutableList(), secondOutput, thirdOutput, "prog3").run()
    }

    GlobalScope.launch {
        Program(programInstructions.toMutableList(), thirdOutput, fourthOutput, "prog4").run()
    }

    GlobalScope.launch {
        Program(programInstructions.toMutableList(), fourthOutput, fifthOutput, "prog5").run()
    }

    return fifthOutput.receive()
}

fun <T> generateCombinationsOf(list: List<T>): List<List<T>> =
    take1AndRecurse(emptyList(), list)

fun <T> take1AndRecurse(selected: List<T>, unSelected: List<T>): List<List<T>> =
    if (unSelected.isEmpty()) {
        listOf(selected)
    } else {
        unSelected.flatMap { take1AndRecurse(selected.plus(it), unSelected.filter { x -> it != x }) }
    }