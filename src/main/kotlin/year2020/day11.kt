package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.productOf
import utils.collections.takeWhileInclusive
import utils.collections.untilStable
import java.lang.StringBuilder

fun main() {

    AirportLoungeSimulator(4, 1).simulate().also { displayPart1(it.numberOfOccupiedChairs()) }
    AirportLoungeSimulator(5).simulate().also { displayPart2(it.numberOfOccupiedChairs()) }

}

class AirportLoungeSimulator(tolerance: Int, private val sightLimit: Int = Int.MAX_VALUE) {

    private val initialLounge = readLinesFromFile("2020/day11.txt").asLoungeWithTolerance(tolerance, sightLimit)

    fun simulate(): AirportLounge = generateSequence(initialLounge) { it.iterate() }.untilStable()
}

class AirportLounge (private val floorplan: Map<Coord, Position>, val sightLength: Int) {

    fun iterate(): AirportLounge =
        floorplan.mapValues { (coord, position) ->

            val occupiedChairs = coord.linesOfSight().count { los -> positionFromSequence(los)?.isOccupied() ?: false }

            position.next(occupiedChairs)

        }.let { AirportLounge(it.toMap(), sightLength) }

    fun numberOfOccupiedChairs() = floorplan.values.count { it.isOccupied() }

    private fun positionFromSequence(coords: Sequence<Coord>): Position? =
        coords.map { floorplan[it] }
            .takeWhileInclusive { it is Floor }
            .take(sightLength)
            .last()

    override fun equals(other: Any?): Boolean {
        return other is AirportLounge && floorplan == other.floorplan
    }

    override fun toString(): String {
        val maxX: Int = floorplan.keys.map { (x) -> x }.maxOrNull() ?: 0
        val maxY: Int = floorplan.keys.map { (_, y) -> y }.maxOrNull() ?: 0

        val sb = StringBuilder()

        (0..maxY).forEach { y -> sb.append((0..maxX).joinToString("") { x -> floorplan[Coord(x, y)]?.toString() ?: "" }) }
        sb.append("\n")

        return sb.toString()
    }

    override fun hashCode(): Int {
        var result = floorplan.hashCode()
        result = 31 * result + sightLength
        return result
    }
}

fun Char.asChairOrFloor(chairTolerance: Int) = when(this) {
    '.' -> Floor
    'L' -> Chair(false, chairTolerance)
    else -> throw RuntimeException("$this is neither chair nor floor")
}

sealed class Position {
    abstract fun isOccupied(): Boolean
    abstract fun next(occupiedNeighbours: Int): Position
}
data class Chair(val occupied: Boolean, val tolerance: Int): Position() {
    override fun isOccupied() = occupied
    override fun next(occupiedNeighbours: Int) =
        when  {
            occupiedNeighbours == 0 -> Chair(true, tolerance)
            occupiedNeighbours >= tolerance -> Chair(false, tolerance)
            else -> Chair(occupied, tolerance)
        }

    override fun toString() = if (occupied) "#" else "L"
}

object Floor: Position() {
    override fun isOccupied() = false
    override fun next(occupiedNeighbours: Int) = Floor

    override fun toString() = "."
}


data class Direction(val deltaX: Int, val deltaY: Int)

val allDirections = productOf(-1..1, -1..1)
    .filter { (x, y) -> x != 0 || y != 0 }
    .map{ (x, y) -> Direction(x, y) }

data class Coord(val x: Int, val y: Int) {

    fun stepInDirection(direction: Direction): Coord = Coord(x + direction.deltaX, y + direction.deltaY)

    fun linesOfSight() = allDirections.map { direction ->
        generateSequence(this.stepInDirection(direction)) { it.stepInDirection(direction) }
    }
}

fun List<String>.asLoungeWithTolerance(tolerance: Int, sightLength: Int) =
    this.flatMapIndexed { y, row -> row.mapIndexed() { x, char ->  Coord(x, y) to char.asChairOrFloor(tolerance) } }
        .let { AirportLounge(it.toMap(), sightLength)}