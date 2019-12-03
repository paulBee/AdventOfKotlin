package coOrdinates

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
}

val theOrigin = Coordinate(0, 0)