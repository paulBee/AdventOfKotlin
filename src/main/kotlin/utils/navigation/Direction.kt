package utils.navigation

import Circular

sealed class Direction: Circular<Direction>, CoordinateMove

interface CoordinateMove {
    fun deltaX() = 0
    fun deltaY() = 0
    fun moveFrom(coordinate: Coordinate): Coordinate =
        Coordinate(coordinate.x + deltaX(), coordinate.y + deltaY())
    fun sequenceFrom(coordinate: Coordinate) = generateSequence(coordinate) { moveFrom(it) }
}

interface Rotation

object Up: Direction() {

    override val previous = Left
    override val next = Right
    override fun deltaY() = -1

    override fun toString() = "Up"
}

object Down: Direction() {
    override val previous = Right
    override val next = Left
    override fun deltaY() = 1

    override fun toString() = "Down"
}

object Left: Direction(), Rotation {
    override val previous = Down
    override val next = Up
    override fun deltaX() = -1

    override fun toString() = "Left"
}

object Right: Direction(), Rotation {
    override val previous = Up
    override val next = Down
    override fun deltaX() = 1

    override fun toString() = "Right"
}

object UpRightDiagonal: CoordinateMove {
    override fun deltaX() = 1
    override fun deltaY() = -1
}

object UpLeftDiagonal: CoordinateMove {
    override fun deltaX() = -1
    override fun deltaY() = -1
}

object DownRightDiagonal: CoordinateMove {
    override fun deltaX() = 1
    override fun deltaY() = 1
}

object DownLeftDiagonal: CoordinateMove {
    override fun deltaX() = -1
    override fun deltaY() = 1
}

val diagonalDirections = listOf(UpRightDiagonal, UpLeftDiagonal, DownRightDiagonal, DownLeftDiagonal)

val orthogonalDirections = listOf(Up, Down, Left, Right)

fun Char.toDirection() = this.toString().toDirection()
fun String.toDirection() : Direction =
    when (this) {
        "U" -> Up
        "D" -> Down
        "R" -> Right
        "L" -> Left
        else -> throw java.lang.IllegalArgumentException("String $this cannot be represented by a direction")
    }