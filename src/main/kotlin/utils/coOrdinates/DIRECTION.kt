package utils.coOrdinates

enum class DIRECTION {
    LEFT, RIGHT, UP, DOWN;

    fun turn(direction: DIRECTION) =
        when (this) {
            UP -> if (direction == LEFT) LEFT else RIGHT
            DOWN -> if (direction == LEFT) RIGHT else LEFT
            LEFT -> if (direction == LEFT) DOWN else UP
            RIGHT -> if (direction == LEFT) UP else DOWN
        }

    override fun toString() =
        when (this) {
            UP -> "U"
            DOWN -> "D"
            LEFT -> "L"
            RIGHT -> "R"
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