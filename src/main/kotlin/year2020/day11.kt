package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.productOf
import utils.collections.takeWhileInclusive

fun main() {
    val input = readLinesFromFile("2020/day11.txt")

    generateSequence(input.asLoungeWithTolerance(4)) { it.iterateByAdjacent() }
        .untilStable()
        .also { displayPart1(it.numberOfOccupiedChairs()) }

    generateSequence(input.asLoungeWithTolerance(5)) { it.iterateByLineOfSight().also { it.print() } }
        .untilStable()
        .also { displayPart2(it.numberOfOccupiedChairs()) }

}
typealias AirportLounge = Map<Coord, Position>

private fun Sequence<AirportLounge>.untilStable(): AirportLounge =
    this.zipWithNext()
        .takeWhileInclusive { (a, b) -> a != b }
        .last().let { (it) -> it }

fun AirportLounge.iterateByAdjacent(): AirportLounge =
    this.map { (coord, position) ->
        coord to
        position.next(coord.linesOfSight().count {
            it.map { this[it] } .take(1).any { it?.isOccupied() ?: false }
        })
    }.toMap()

fun AirportLounge.iterateByLineOfSight(): AirportLounge =
    this.map { (coord, position) ->
        coord to
                position.next(coord.linesOfSight().count {
                    it.map { this[it] } .takeWhileInclusive { it is Floor }.any { it?.isOccupied() ?: false }
                })
    }.toMap()

fun AirportLounge.numberOfOccupiedChairs() = this.values.count { it.isOccupied() }

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

    fun adjacentCoords() =
        productOf(x-1..x+1, y-1..y+1)
            .map { (x, y) -> Coord(x, y) }
            .filter { it != this}
}

fun List<String>.asLoungeWithTolerance(tolerance: Int) =
    this.flatMapIndexed { y, row -> row.mapIndexed() { x, char ->  Coord(x, y) to char.asChairOrFloor(tolerance) } }
        .toMap()


fun Map<Coord, Position>.print() {
    val maxX: Int = this.keys.map { (x) -> x }.maxOrNull() ?: 0
    val maxY: Int = this.keys.map { (_, y) -> y }.maxOrNull() ?: 0

    println()
    (0..maxY).forEach { y ->
        println((0..maxX).map {x -> this[Coord(x, y)]!!.toString() }.joinToString(""))
    }
    println()
}