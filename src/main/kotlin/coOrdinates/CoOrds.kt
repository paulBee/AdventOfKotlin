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

    fun manHattenDistanceTo(other: CoOrdinate): Int =
        abs(this.x - other.x) + abs(this.y - other.y)

    fun isOrigin(): Boolean = this.x == 0 && this.y == 0
}

val theOrigin = CoOrdinate(0, 0)

val moveRegex = """^([UDRL])(\d+)$""".toRegex()

fun String.toMoveInstruction() : MoveInstruction {
    val (direction, distance) = moveRegex.matchEntire(this)?.destructured ?: throw IllegalArgumentException("String $this does not match regex")
    return MoveInstruction(direction = direction.toDirection(), distance = distance.toInt())
}

data class MoveInstruction (val direction : DIRECTION, val distance : Int)

enum class DIRECTION {
    LEFT, RIGHT, UP, DOWN;
}

fun String.toDirection() : DIRECTION =
    when (this) {
        "U" -> DIRECTION.UP
        "D" -> DIRECTION.DOWN
        "R" -> DIRECTION.RIGHT
        "L" -> DIRECTION.LEFT
        else -> throw java.lang.IllegalArgumentException("String $this cannot be represented by a direction")
    }
