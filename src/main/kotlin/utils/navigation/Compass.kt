package utils.navigation

import Circular
import year2020.Rotate

sealed class Compass: Circular<Compass> {
    fun turn(rotate: Rotate) = when (rotate) {
        Rotate.Left -> previous
        Rotate.Right -> next
    }

    abstract fun travel(coordinate: Coordinate, amount: Int): Coordinate
    fun travel(coordinate: Coordinate) = travel(coordinate, 1)
}
object North : Compass() {
    override fun travel(coordinate: Coordinate, amount: Int) = coordinate.moveDistance(DIRECTION.UP, amount)
    override val previous = West
    override val next = East
    override fun toString() = "North"
}
object South : Compass() {
    override fun travel(coordinate: Coordinate, amount: Int) = coordinate.moveDistance(DIRECTION.DOWN, amount)
    override val previous = East
    override val next = West
    override fun toString() = "South"
}
object East : Compass() {
    override fun travel(coordinate: Coordinate, amount: Int) = coordinate.moveDistance(DIRECTION.RIGHT, amount)
    override val previous = North
    override val next = South
    override fun toString() = "East"
}
object West : Compass() {
    override fun travel(coordinate: Coordinate, amount: Int) = coordinate.moveDistance(DIRECTION.LEFT, amount)
    override val previous = South
    override val next = North
    override fun toString() = "West"
}