package utils.navigation

import Circular

sealed class Compass: Circular<Compass> {
    abstract fun travel(coordinate: Coordinate, amount: Int): Coordinate
    fun travel(coordinate: Coordinate) = travel(coordinate, 1)
}
object North : Compass() {
    override fun travel(coordinate: Coordinate, amount: Int) = coordinate.moveDistance(Up, amount)
    override val previous = West
    override val next = East
    override fun toString() = "North"
}
object South : Compass() {
    override fun travel(coordinate: Coordinate, amount: Int) = coordinate.moveDistance(Down, amount)
    override val previous = East
    override val next = West
    override fun toString() = "South"
}
object East : Compass() {
    override fun travel(coordinate: Coordinate, amount: Int) = coordinate.moveDistance(Right, amount)
    override val previous = North
    override val next = South
    override fun toString() = "East"
}
object West : Compass() {
    override fun travel(coordinate: Coordinate, amount: Int) = coordinate.moveDistance(Left, amount)
    override val previous = South
    override val next = North
    override fun toString() = "West"
}