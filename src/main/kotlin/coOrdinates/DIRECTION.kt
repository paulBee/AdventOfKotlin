package coOrdinates

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