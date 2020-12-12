package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.takeWhileInclusive
import utils.collections.untilStable
import utils.navigation.Coordinate
import utils.navigation.diagonalDirections
import utils.navigation.gridToString
import utils.navigation.orthogonalDirections
import java.lang.StringBuilder

fun main() {

    AirportLoungeSimulator(4, 1).simulate().also { displayPart1(it.numberOfOccupiedChairs()) }
    AirportLoungeSimulator(5).simulate().also { displayPart2(it.numberOfOccupiedChairs()) }

}

class AirportLoungeSimulator(tolerance: Int, sightLimit: Int = Int.MAX_VALUE) {

    private val initialLounge = readLinesFromFile("2020/day11.txt").asLoungeWithTolerance(tolerance, sightLimit)

    fun simulate(): AirportLounge = generateSequence(initialLounge) { it.iterate() }.untilStable()
}

class AirportLounge (private val floorplan: Map<Coordinate, LoungeSpace>, private val sightLength: Int) {

    fun iterate(): AirportLounge =
        floorplan.mapValues { (coord, position) ->

            val occupiedChairs = coord.linesOfSight().count { los -> positionFromSequence(los)?.isOccupied() ?: false }

            position.next(occupiedChairs)

        }.let { AirportLounge(it.toMap(), sightLength) }

    fun numberOfOccupiedChairs() = floorplan.values.count { it.isOccupied() }

    private fun positionFromSequence(coords: Sequence<Coordinate>): LoungeSpace? =
        coords.map { floorplan[it] }
            .takeWhileInclusive { it is Floor }
            .take(sightLength)
            .last()

    override fun equals(other: Any?): Boolean {
        return other is AirportLounge && floorplan == other.floorplan
    }

    override fun toString() = floorplan.gridToString()

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

sealed class LoungeSpace {
    abstract fun isOccupied(): Boolean
    abstract fun next(occupiedNeighbours: Int): LoungeSpace
}
data class Chair(val occupied: Boolean, val tolerance: Int): LoungeSpace() {
    override fun isOccupied() = occupied
    override fun next(occupiedNeighbours: Int) =
        when  {
            occupiedNeighbours == 0 -> Chair(true, tolerance)
            occupiedNeighbours >= tolerance -> Chair(false, tolerance)
            else -> Chair(occupied, tolerance)
        }

    override fun toString() = if (occupied) "#" else "L"
}

object Floor: LoungeSpace() {
    override fun isOccupied() = false
    override fun next(occupiedNeighbours: Int) = Floor

    override fun toString() = "."
}

fun Coordinate.linesOfSight() = orthogonalDirections.plus(diagonalDirections).map { it.sequenceFrom(this).drop(1) }

fun List<String>.asLoungeWithTolerance(tolerance: Int, sightLength: Int) =
    this.flatMapIndexed { y, row -> row.mapIndexed() { x, char ->  Coordinate(x, y) to char.asChairOrFloor(tolerance) } }
        .let { AirportLounge(it.toMap(), sightLength)}