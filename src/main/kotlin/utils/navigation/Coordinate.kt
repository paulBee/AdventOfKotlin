package utils.navigation

import utils.algorithm.highestCommonFactor
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.StringBuilder
import kotlin.math.abs

data class Coordinate(val x : Int, val y : Int) {

    fun moveDistance(direction: Direction, it: Int): Coordinate =
        generateSequence(this) { direction.moveFrom(it) }.elementAt(it)

    fun move(dx: Int, dy: Int) = Coordinate(x + dx, y + dy)
    fun move(direction: Direction) = moveDistance(direction, 1)

    fun rotate(rotate: Rotation) =
        when (rotate) {
            Left -> Coordinate(y, -x)
            Right -> Coordinate(-y, x)
            else -> throw RuntimeException("Unsupported Rotation")
        }

    fun manhattanDistanceTo(other: Coordinate): Int = abs(this.x - other.x) + abs(this.y - other.y)
    fun manhattanDisplacement() = manhattanDistanceTo(theOrigin)

    fun isOrigin(): Boolean = this == theOrigin

    fun follow(direction: DirectionRatio) = move(direction.deltaX, direction.deltaY)

    fun allAdjacent(): List<Coordinate> = orthognals

    val orthognals by lazy { orthogonalDirections.map { it.moveFrom(this) } }
    val diagonals by lazy { diagonalDirections.map { it.moveFrom(this) } }

    companion object {
        fun at(x: Int, y: Int): Coordinate = Coordinate(x, y)
        fun at(x: String, y: String): Coordinate = at(x.toInt(), y.toInt())

        fun of(string: String) = string
            .split(",")
            .map { it.trim() }
            .let { (x, y) -> at(x, y) }
        fun of(input: Pair<String, String>) = at (input.first, input.second)
    }

    fun coordsBetween(other: Coordinate): Sequence<Coordinate> {

        return if (this.x == other.x) {
            val (higher, lower) = listOf(this, other).sortedBy { it.y }
            Down.sequenceFrom(higher).take((lower.y - higher.y) + 1)
        } else if (this.y == other.y) {
            val (left, right) = listOf(this, other).sortedBy { it.x }
            Right.sequenceFrom(left).take((right.x - left.x) + 1 )
        } else {
            throw IllegalArgumentException("Can only produce straight lines")
        }
    }
}

val theOrigin = Coordinate(0, 0)

// positive x is right, positive y is down
data class DirectionRatio(val deltaX: Int, val deltaY: Int) {

    fun toSimplestForm(): DirectionRatio {
        val dividingRatio = highestCommonFactor(deltaX, deltaY)
        return DirectionRatio(deltaX / dividingRatio, deltaY / dividingRatio)
    }

    fun quadrant(): QUADRANT =
        when {
            deltaY < 0 && deltaX >= 0 -> QUADRANT.UPPER_RIGHT
            deltaY >= 0 && deltaX > 0 -> QUADRANT.LOWER_RIGHT
            deltaY > 0 && deltaX <= 0 -> QUADRANT.LOWER_LEFT
            deltaY <= 0 && deltaX < 0 -> QUADRANT.UPPER_LEFT
            else -> throw IllegalStateException("No direction points this way!... apart from $this")
        }
}

enum class QUADRANT { UPPER_RIGHT, LOWER_RIGHT, LOWER_LEFT, UPPER_LEFT }

fun Map<Coordinate, Any>.gridToString(): String {
    val maxX: Int = this.keys.map { (x) -> x }.maxOrNull() ?: 0
    val maxY: Int = this.keys.map { (_, y) -> y }.maxOrNull() ?: 0

    val sb = StringBuilder()

    (0..maxY).forEach { y -> sb.append((0..maxX).joinToString("") { x -> this[Coordinate(x, y)]?.toString() ?: "" }) }
    sb.append("\n")

    return sb.toString()
}

fun <T> Map<Coordinate, T>.gridToString(stringify: (T?) -> String): String {
    val maxX: Int = this.keys.maxOfOrNull { (x) -> x } ?: 0
    val minX: Int = this.keys.minOfOrNull { (x) -> x } ?: 0
    val maxY: Int = this.keys.maxOfOrNull { (_, y) -> y } ?: 0
    val minY: Int = this.keys.minOfOrNull { (_, y) -> y } ?: 0

    val sb = StringBuilder()

    sb.append("Top Left Coord == ($minX,$minY)\n\n\n")

    (minY..maxY).forEach { y -> sb.append((minX..maxX).joinToString("") { x -> stringify(this[Coordinate(x, y)]) }).append("\n") }
    sb.append("\n")

    return sb.toString()
}