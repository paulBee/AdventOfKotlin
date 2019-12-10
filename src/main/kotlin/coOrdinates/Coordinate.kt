package coOrdinates

import java.lang.IllegalStateException
import kotlin.math.abs

data class Coordinate(val x : Int, val y : Int) {

    fun coordsInDirection(moveInstruction: MoveInstruction) : List<Coordinate> =
        (1..moveInstruction.distance)
            .map {
                when(moveInstruction.direction) {
                    DIRECTION.UP -> Coordinate(x, y + it)
                    DIRECTION.DOWN -> Coordinate(x, y - it)
                    DIRECTION.LEFT -> Coordinate(x - it, y)
                    DIRECTION.RIGHT -> Coordinate(x + it, y)
                }
            }

    fun manhattanDistanceTo(other: Coordinate): Int =
        abs(this.x - other.x) + abs(this.y - other.y)

    fun isOrigin(): Boolean = this == theOrigin

    fun follow(direction: DirectionRatio) = Coordinate(this.x + direction.deltaX, this.y + direction.deltaY)

}

val theOrigin = Coordinate(0, 0)

// positive x is right, positive y is down
data class DirectionRatio(val deltaX: Int, val deltaY: Int) {
    fun toSimplestForm(): DirectionRatio {
        val dividingRatio = hcf(deltaX, deltaY)
        return DirectionRatio(deltaX / dividingRatio, deltaY / dividingRatio)
    }

    fun pickQuadrant(): QUADRANT =
        when {
            deltaY < 0 && deltaX >= 0 -> QUADRANT.UPPER_RIGHT
            deltaY >= 0 && deltaX > 0 -> QUADRANT.LOWER_RIGHT
            deltaY > 0 && deltaX <= 0 -> QUADRANT.LOWER_LEFT
            deltaY <= 0 && deltaX < 0 -> QUADRANT.UPPER_LEFT
            else -> throw IllegalStateException("No direction points this way!... apart from $this")
        }

}

enum class QUADRANT { UPPER_RIGHT, LOWER_RIGHT, LOWER_LEFT, UPPER_LEFT }

fun hcf(n1: Int, n2: Int): Int =
    if (n2 != 0)
        hcf(n2, n1 % n2)
    else
        n1