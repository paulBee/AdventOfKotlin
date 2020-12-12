package year2019

import utils.navigation.Coordinate
import year2019.intcodeComputers.Program
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import utils.aoc.readProgramInstructions
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@ExperimentalCoroutinesApi
suspend fun main() {

    withContext(Dispatchers.Default) {
        val programInstructions = readProgramInstructions("2019/day13.txt")
        val inputChannel = Channel<Long>(Int.MAX_VALUE)
        val outputChanel = Channel<Long>(Int.MAX_VALUE)
        val program = Program(programInstructions, inputChannel, outputChanel)
        val screen = HashMap<Coordinate, Pixel>()

        var progRunning = true
        var blocksRemaining = true
        while(progRunning && blocksRemaining) {
            val paused = program.runUntilInput()
            updateScreen(outputChanel, screen)
            val gameState = evaluateGameState(screen)
            val (ballX, paddleX, numberOfBlocks, _) = gameState
            when {
                paddleX.x < ballX.x -> inputChannel.send(1L)
                paddleX.x > ballX.x -> inputChannel.send(-1L)
                else -> inputChannel.send(0L)
            }
            progRunning = !paused.completed
            blocksRemaining = numberOfBlocks > 0
        }

    }

}

fun evaluateGameState(screen: HashMap<Coordinate, Pixel>): GameState {

    val ballX = screen.entries.first { it.value.type() == PixelType.BALL }.key
    val paddleX = screen.entries.filter { it.value.type() == PixelType.PADDLE }.map { it.key }
    val numberOfBlocks = screen.values.count { it.type() == PixelType.BLOCK }
    val currentScore = screen.getOrDefault(Coordinate(-1, 0), 0)

    return GameState(ballX, paddleX[0], numberOfBlocks, currentScore.toInt())

}
val scoreCoord = Coordinate(-1, 0)
@ExperimentalCoroutinesApi
suspend fun updateScreen(outputChanel: Channel<Long>, screen: HashMap<Coordinate, Pixel>) {
    drainOutput(outputChanel).chunked(3)
        .forEach {
            val coordinate = Coordinate(it[0].toInt(), it[1].toInt())
            if (coordinate == scoreCoord) {
                println("score is now ${it[2]}")
            } else {
                screen[coordinate] = it[2]
            }
        }
}

data class GameState (val ballX: Coordinate, val paddleX: Coordinate, val numberOfBlocks: Int, val currentScore: Int)

typealias Pixel = Long

fun Pixel.type() =
    when (this) {
        0L -> PixelType.EMPTY
        1L -> PixelType.WALL
        2L -> PixelType.BLOCK
        3L -> PixelType.PADDLE
        4L -> PixelType.BALL
        else -> PixelType.SCORE
    }

enum class PixelType { EMPTY, WALL, BLOCK, PADDLE, BALL, SCORE }

@ExperimentalCoroutinesApi
private suspend fun drainOutput(source: Channel<Long>): List<Long> {
    // PRs very welcome for a cleaner way to do this
    val outputs = ArrayList<Long>()
    while (!source.isEmpty) {
        outputs.add(source.receive())
    }
    return outputs
}