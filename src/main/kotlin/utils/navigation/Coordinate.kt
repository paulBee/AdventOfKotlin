package utils.navigation

import utils.algorithm.highestCommonFactor
import java.lang.IllegalStateException
import java.lang.StringBuilder
import kotlin.math.abs

data class Coordinate(val x : Int, val y : Int) {

    fun moveDistance(direction: Direction, it: Int): Coordinate =
        generateSequence(this) { direction.moveFrom(it) }.elementAt(it)

    fun move(dx: Int, dy: Int) = Coordinate(x + dx, y + dy)

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

    fun allAdjacent(): List<Coordinate> = orthogonalDirections.map { it.moveFrom(this) }

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