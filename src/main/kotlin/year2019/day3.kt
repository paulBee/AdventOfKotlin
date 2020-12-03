package year2019

import coOrdinates.Coordinate
import coOrdinates.MoveInstruction
import coOrdinates.theOrigin
import coOrdinates.toMoveInstruction
import readLinesFromFile

fun main () {
    val (moveInstructions1, moveInstructions2) = getWireInstructions()

    val wire1 = buildWire(moveInstructions1)
    val wire2 = buildWire(moveInstructions2)

    val intersections = findIntersections(wire1, wire2)

    val manhattanAnswer = nearestManhattanDistance(intersections)
    val timingAnswer = intersections
        .map { wire1.indexOf(it) + wire2.indexOf(it) }
        .min()

    println("The closest intersection to the origin is $manhattanAnswer")
    println("The closest intersection to the start of the wires is $timingAnswer")
}

fun nearestManhattanDistance(intersections: List<Coordinate>) =
    intersections
        .map { it.manhattanDistanceTo(theOrigin) }
        .min()

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