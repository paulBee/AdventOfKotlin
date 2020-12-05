package year2019

import utils.collections.chunkWhen
import utils.coOrdinates.Coordinate
import utils.coOrdinates.DIRECTION
import year2019.intcodeComputers.Program
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import utils.aoc.readProgramInstructions

@ExperimentalCoroutinesApi
suspend fun main() {
    val instructions = readProgramInstructions("2019/day17.txt")

    val output = Channel<Long>(Int.MAX_VALUE)

    val pixels = HashMap<Coordinate, String>()

    withContext(Dispatchers.Default) {
        Program(instructions, output = output).run()
    }

    val outputValues = year2019.intcodeComputers.drainOutput(output)

    outputValues.chunkWhen { _, it -> it == 10L }
        .mapIndexed { y, line ->
            line.filter { it != 10L }.forEachIndexed() { x, it ->
                pixels[Coordinate(x, y)] = it.toChar().toString()
            }
        }


    printMapfoo(pixels)

    part1(pixels)
    part2Diagnostics(pixels)
    part2PostHuman()

}

fun part2Diagnostics(pixels: Map<Coordinate, String>) {

    val startingCoordinate = pixels.entries
        .find { it.value == "^" }?.key
        ?: throw RuntimeException("this is not the droid you are looking for")

    val startingDirection = DIRECTION.UP

    val pathString = followPath(startingCoordinate, startingDirection, pixels)

    print(pathString)
}

fun followPath(coordinate: Coordinate, direction: DIRECTION, pixels: Map<Coordinate, String>, paths: List<String> = emptyList()): List<String> {
    val leftDir = direction.turn(DIRECTION.LEFT)
    val rightDir = direction.turn(DIRECTION.RIGHT)
    return when {
        pixels[coordinate.moveDistanceNegativeUP(leftDir, 1)] == "#" -> {
            val (pathSection, newCoord) = follow(coordinate, leftDir, pixels)
            followPath(newCoord, leftDir, pixels, paths.plus("L$pathSection"))
        }
        pixels[coordinate.moveDistanceNegativeUP(rightDir, 1)] == "#" -> {
            val (pathSection, newCoord) = follow(coordinate, rightDir, pixels)
            followPath(newCoord, rightDir, pixels, paths.plus("R$pathSection"))
        }
        else -> return paths
    }
}

//A, C, C, A, B, A, A, B, C, B
//
//A
//R8, L12, R8
//B
//R8, L8, L8, R8, R10
//C
//R12, L8, R10,
@ExperimentalCoroutinesApi
private suspend fun part2PostHuman() {
    val mainRoutine = "A,C,C,A,B,A,A,B,C,B"
    val funcA = "R,8,L,12,R,8"
    val funcB = "R,8,L,8,L,8,R,8,R,10"
    val funcC = "R,12,L,8,R,10"
    val input = Channel<Long>(Int.MAX_VALUE)
    val output = Channel<Long>(Int.MAX_VALUE)
    GlobalScope.launch {
        mainRoutine.forEach { input.send(it.toLong()) }
        input.send(10L)
        funcA.forEach { input.send(it.toLong()) }
        input.send(10L)
        funcB.forEach { input.send(it.toLong()) }
        input.send(10L)
        funcC.forEach { input.send(it.toLong()) }
        input.send(10L)
        input.send('y'.toLong())
        input.send(10L)
    }

    val instructions = readProgramInstructions("2019/day17.txt").toMutableList()
    instructions[0] = 2L

    withContext(Dispatchers.Default) {
        Program(
            instructions,
            input,
            output
        ).run()
    }

    val outputFromProg = withContext(Dispatchers.Default) { year2019.intcodeComputers.drainOutput(output) }
    outputFromProg.forEach { print(it.toChar()) }
    println(outputFromProg.last())

}

private fun follow(
    coordinate: Coordinate,
    rightDir: DIRECTION,
    pixels: Map<Coordinate, String>
): Pair<Int, Coordinate> {
    val path = generateSequence(coordinate) { it.moveDistanceNegativeUP(rightDir, 1) }
        .drop(1)
        .takeWhile { pixels[it] == "#" }
        .toList()
    val pathSection = path.size
    val newCoord = path.last()
    return Pair(pathSection, newCoord)
}

private fun part1(pixels: HashMap<Coordinate, String>) {
    println(pixels.filter { it.value == "#" && it.key.allAdjacent().all { adjacent -> pixels[adjacent] == "#" } }
        .map { it.key }
        .sumBy { it.x * it.y })
}

fun printMapfoo(knownPixels: MutableMap<Coordinate, String>) {
    val xPixels = knownPixels.map { it.key.x }
    val yPixels = knownPixels.map { it.key.y }

    val minx = xPixels.maxOrNull()?: throw RuntimeException(" no pixels")
    val maxx = xPixels.maxOrNull()?: throw RuntimeException(" no pixels")
    val miny = yPixels.maxOrNull()?: throw RuntimeException(" no pixels")
    val maxy = yPixels.maxOrNull()?: throw RuntimeException(" no pixels")

    (miny..maxy).forEach { y ->
        println((minx..maxx).map { x -> knownPixels.getOrDefault(Coordinate(x, y), "W") }
            .joinToString(""))
    }
}