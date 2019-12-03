import coOrdinates.CoOrdinate
import coOrdinates.MoveInstruction
import coOrdinates.theOrigin
import coOrdinates.toMoveInstruction
import java.lang.IllegalArgumentException

fun main () {
    val (firstInstructions, secondInstructions) = getWireInstructions()
    val wire1 = buildWire(firstInstructions)
    val wire2 = buildWire(secondInstructions)

    val intersections = findIntersections(wire1, wire2)
    val manhattanAnswer = nearestManhattanDistance(intersections)

    val minByTiming = intersections
        .map { wire1.indexOf(it) + wire2.indexOf(it) }
        .min()

    println(manhattanAnswer)
    println(minByTiming)
}



fun nearestManhattanDistance(intersections: List<CoOrdinate>) =
    intersections
        .map { it.manHattenDistanceTo(theOrigin) }
        .min()?: throw IllegalArgumentException("These lines dont intersect fool!")


fun findIntersections(
    wire1: List<CoOrdinate>,
    wire2: List<CoOrdinate>
) = wire1.intersect(wire2)
    .filter { !it.isOrigin() }

fun buildWire(firstInstruction: List<MoveInstruction>) : List<CoOrdinate> =
    firstInstruction.fold(listOf(CoOrdinate(0,0))) {
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