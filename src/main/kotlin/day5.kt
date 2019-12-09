import intcodeComputers.Program
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
fun main() = runBlocking<Unit> {
    val instructions = readTextFromFile("day5.txt").split(",").map { it.toLong() }

    val channel = Channel<Long>()
    channel.send(5L)
    println(Program(instructions.toMutableList(), channel).run())

}