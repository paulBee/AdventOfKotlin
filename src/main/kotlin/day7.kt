import intcodeComputers.Program
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

@ExperimentalCoroutinesApi
fun main() = runBlocking<Unit> {
    val programInstructions = readTextFromFile("day7.txt").split(",").map { it.toLong() }

    val max = generateCombinationsOf(listOf(5, 6, 7, 8, 9))
        .map {
            val guess = runPhaseGuess(programInstructions, it)
            println("$it = $guess")
            guess

        }
        .max()

    println(max)


}

@ExperimentalCoroutinesApi
private suspend fun runPhaseGuess(programInstructions: List<Long>, phaseGuess: List<Int>): Long {
    val (phase1, phase2, phase3, phase4, phase5) = phaseGuess
    val firstOutput = Channel<Long>(Channel.UNLIMITED)
    val secondOutput = Channel<Long>(Channel.UNLIMITED)
    val thirdOutput = Channel<Long>(Channel.UNLIMITED)
    val fourthOutput = Channel<Long>(Channel.UNLIMITED)
    val fifthOutput = Channel<Long>(Channel.UNLIMITED)
    fifthOutput.send(phase1.toLong())
    firstOutput.send(phase2.toLong())
    secondOutput.send(phase3.toLong())
    thirdOutput.send(phase4.toLong())
    fourthOutput.send(phase5.toLong())
    fifthOutput.send(0L)
    GlobalScope.launch {
        Program(programInstructions.toMutableList(), fifthOutput, firstOutput, "prog1").run()
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

    val program5return = GlobalScope.async {
        Program(programInstructions.toMutableList(), fourthOutput, fifthOutput, "prog5").run()
    }.await()

    return program5return
}

fun <T> generateCombinationsOf(list: List<T>): List<List<T>> =
    take1AndRecurse(emptyList(), list)

fun <T> take1AndRecurse(selected: List<T>, unSelected: List<T>): List<List<T>> =
    if (unSelected.isEmpty()) {
        listOf(selected)
    } else {
        unSelected.flatMap { take1AndRecurse(selected.plus(it), unSelected.filter { x -> it != x }) }
    }