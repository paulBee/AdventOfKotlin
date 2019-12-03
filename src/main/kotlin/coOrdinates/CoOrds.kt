package coOrdinates

import kotlin.math.abs

data class CoOrdinate(val x : Int, val y : Int) {

    fun coordsInDirection(moveInstruction: MoveInstruction) : List<CoOrdinate> =
        (1..moveInstruction.distance)
            .map {
                when(moveInstruction.direction) {
                    DIRECTION.UP -> CoOrdinate(x, y + it)
                    DIRECTION.DOWN -> CoOrdinate(x, y - it)
                    DIRECTION.LEFT -> CoOrdinate(x - it, y)
                    DIRECTION.RIGHT -> CoOrdinate(x + it, y)
                }
            }

    fun manhattanDistanceTo(other: CoOrdinate): Int =
        abs(this.x - other.x) + abs(this.y - other.y)

    fun isOrigin(): Boolean = this == theOrigin
}

val theOrigin = CoOrdinate(0, 0)