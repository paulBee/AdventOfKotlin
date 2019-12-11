package coOrdinates

enum class DIRECTION {
    LEFT, RIGHT, UP, DOWN;

    fun turn(direction: DIRECTION) =
        when (this) {
            UP -> if (direction == LEFT) LEFT else RIGHT
            DOWN -> if (direction == LEFT) RIGHT else LEFT
            LEFT -> if (direction == LEFT) DOWN else UP
            RIGHT -> if (direction == LEFT) UP else DOWN
        }
}

fun String.toDirection() : DIRECTION =
    when (this) {
        "U" -> DIRECTION.UP
        "D" -> DIRECTION.DOWN
        "R" -> DIRECTION.RIGHT
        "L" -> DIRECTION.LEFT
        else -> throw java.lang.IllegalArgumentException("String $this cannot be represented by a direction")
    }