import intcodeComputers.Program
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
fun main() = runBlocking<Unit> {
    val instructions = readTextFromFile("day5.txt").split(",").map { it.toInt() }

    val channel = Channel<Int>()
    channel.send(5)
    println(Program(instructions.toMutableList(), channel).run())

}