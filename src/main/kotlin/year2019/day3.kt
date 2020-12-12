package year2019

import utils.aoc.readLinesFromFile
import utils.navigation.*

fun main () {
    val (moveInstructions1, moveInstructions2) = getWireInstructions()

    val wire1 = buildWire(moveInstructions1)
    val wire2 = buildWire(moveInstructions2)

    val intersections = findIntersections(wire1, wire2)

    val manhattanAnswer = nearestManhattanDistance(intersections)
    val timingAnswer = intersections
        .map { wire1.indexOf(it) + wire2.indexOf(it) }
        .minOrNull()

    println("The closest intersection to the origin is $manhattanAnswer")
    println("The closest intersection to the start of the wires is $timingAnswer")
}

fun nearestManhattanDistance(intersections: List<Coordinate>) =
    intersections
        .map { it.manhattanDistanceTo(theOrigin) }
        .minOrNull()

fun findIntersections(wire1: List<Coordinate>, wire2: List<Coordinate>) =
    wire1.intersect(wire2).filter { !it.isOrigin() }

fun buildWire(moveInstructions: List<MoveInstruction>) : List<Coordinate> =
    moveInstructions.fold(listOf(theOrigin)) {
            wireSoFar, moveInstruction -> wireSoFar.plus(wireSoFar.last().coordsInDirection(moveInstruction))
    }

fun getWireInstructions() : Pair<List<MoveInstruction>, List<MoveInstruction>> {
    val (line1, line2) = readLinesFromFile("2019/day3.txt")

    return Pair(
        inputLineToMoveInstructions(line1),
        inputLineToMoveInstructions(line2)
    )
}

fun inputLineToMoveInstructions(line: String) =
    line.split(",").map { it.toMoveInstruction() }

data class MoveInstruction (val direction : Direction, val distance : Int)

val moveRegex = """^([UDRL])(\d+)$""".toRegex()
fun String.toMoveInstruction() : MoveInstruction {
    val (direction, distance) =
        moveRegex.matchEntire(this)
            ?.destructured
            ?: throw IllegalArgumentException("String $this does not match regex")

    return MoveInstruction(direction = direction.toDirection(), distance = distance.toInt())
}

fun Coordinate.coordsInDirection(moveInstruction: MoveInstruction) : List<Coordinate> =
    (1..moveInstruction.distance).map { moveDistance(moveInstruction.direction, it) }