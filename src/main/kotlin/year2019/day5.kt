package year2019

import year2019.intcodeComputers.Program
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import utils.aoc.readProgramInstructions

@ExperimentalCoroutinesApi
fun main() = runBlocking<Unit> {
    val instructions = readProgramInstructions("2019/day5.txt")

    val channel = Channel<Long>()
    channel.send(5L)
    println(Program(instructions.toMutableList(), channel).run())

}