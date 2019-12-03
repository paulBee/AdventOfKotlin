import coOrdinates.Coordinate
import coOrdinates.MoveInstruction
import coOrdinates.theOrigin
import coOrdinates.toMoveInstruction

fun main () {
    val (firstInstructions, secondInstructions) = getWireInstructions()

    val wire1 = buildWire(firstInstructions)
    val wire2 = buildWire(secondInstructions)

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

fun buildWire(firstInstruction: List<MoveInstruction>) : List<Coordinate> =
    firstInstruction.fold(listOf(theOrigin)) {
            wireSoFar, moveInstruction -> wireSoFar.plus(wireSoFar.last().coordsInDirection(moveInstruction))
    }



fun getWireInstructions() : Pair<List<MoveInstruction>, List<MoveInstruction>> {
    val (firstInstruction, secondInstruction) = readLinesFromFile("day3.txt")

    return Pair(
        inputLineToMoveInstructions(firstInstruction),
        inputLineToMoveInstructions(secondInstruction)
    )
}

fun inputLineToMoveInstructions(firstInstruction: String) =
    firstInstruction.split(",").map { it.toMoveInstruction() }