import intcodeComputers.Program
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach

@ExperimentalCoroutinesApi
fun main() = runBlocking<Unit> {
    val programInstructions = readTextFromFile("day9.txt").split(",").map { it.toLong() }

    val input = Channel<Long>(Int.MAX_VALUE)
    val output = Channel<Long>(Int.MAX_VALUE)
    input.send(2L)
    launch { output.consumeEach { println("an output $it") } }
    val finalOutput = withContext(Dispatchers.Default) {
        Program(
            programInstructions,
            input,
            output
        ).run()
    }

    println(finalOutput)

    return@runBlocking
}