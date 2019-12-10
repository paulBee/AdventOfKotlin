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

    fun follow(direction: DirectionRatio) = Coordinate(this.x + direction.deltaX, this.y + direction.deltaY)

}

val theOrigin = Coordinate(0, 0)


data class DirectionRatio(val deltaX: Int, val deltaY: Int) {
    fun toSimplestForm(): DirectionRatio {
        val dividingRatio = hcf(deltaX, deltaY)
        return DirectionRatio(deltaX / dividingRatio, deltaY / dividingRatio)
    }
}

fun hcf(n1: Int, n2: Int): Int =
    if (n2 != 0)
        hcf(n2, n1 % n2)
    else
        n1