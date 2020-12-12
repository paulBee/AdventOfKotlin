package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import kotlin.math.absoluteValue

fun main() {
    readLinesFromFile("2020/day12.txt")
        .map { it.toLetterAndNumber() }
        .fold(Boat(0, 0, East))
        { boat, (instruction, amount) ->
            when (instruction) {
                "N" -> boat.move(North, amount)
                "S" -> boat.move(South, amount)
                "E" -> boat.move(East, amount)
                "W" -> boat.move(West, amount)
                "L" -> boat.turn(Left, amount)
                "R" -> boat.turn(Right, amount)
                "F" -> boat.advance(amount)
                else -> throw RuntimeException("wtf is $instruction")
            }.also { println("$instruction $amount -> $it") }
        }.also{ displayPart1(it.manhattanDisplacement()) }

    readLinesFromFile("2020/day12.txt")
        .map { it.toLetterAndNumber() }
        .fold(WaypointBoat(0, 0, Waypoint(10, -1)))
        { boat, (instruction, amount) ->
            when (instruction) {
                "N" -> boat.moveWaypoint(North, amount)
                "S" -> boat.moveWaypoint(South, amount)
                "E" -> boat.moveWaypoint(East, amount)
                "W" -> boat.moveWaypoint(West, amount)
                "L" -> boat.rotateWaypoint(Left, amount)
                "R" -> boat.rotateWaypoint(Right, amount)
                "F" -> boat.advance(amount)
                else -> throw RuntimeException("wtf is $instruction")
            }.also { println("$instruction $amount -> $it") }
        }.also{ displayPart2(it.manhattanDisplacement()) }
}

data class Waypoint(val x: Int, val y: Int) {

    fun move(direction: Compass, length: Int) =
        when (direction) {
            North -> Waypoint(x, y - length)
            South -> Waypoint(x, y + length)
            East -> Waypoint(x + length, y)
            West -> Waypoint(x - length, y)
        }

    fun rotate(hand: Hand) =
        when (hand) {
            Left -> Waypoint(y, -x)
            Right -> Waypoint(-y, x)
        }
}

data class WaypointBoat(val x: Int, val y: Int, val waypoint: Waypoint) {

    fun moveWaypoint(direction: Compass, length: Int) = WaypointBoat(x, y, waypoint.move(direction, length))

    fun rotateWaypoint(hand: Hand, degrees: Int) = WaypointBoat(x, y, (1..(degrees/90)).fold(waypoint) { it, _ -> it.rotate(hand)})

    fun advance(times: Int) = (1..times).fold(this) { boat, _ -> boat.move()}

    fun move() = WaypointBoat(x + waypoint.x, y + waypoint.y, waypoint)

    fun manhattanDisplacement() = x.absoluteValue + y.absoluteValue

}

data class Boat(val x: Int, val y: Int, val facing : Compass) {
    fun move(direction: Compass, length: Int) =
        when (direction) {
            North -> Boat(x, y - length, facing)
            South -> Boat(x, y + length, facing)
            East -> Boat(x + length, y, facing)
            West -> Boat(x - length, y, facing)
        }

    fun turn(hand: Hand, degrees: Int) = Boat(x, y, (1..(degrees/90)).fold(facing) { it, _ -> it.turn(hand)})

    fun advance(length: Int) = move(facing, length)

    fun manhattanDisplacement() = x.absoluteValue + y.absoluteValue

}


sealed class Compass {
    abstract fun turn(hand: Hand): Compass
}
object North : Compass() {
    override fun turn(hand: Hand) =
        when (hand) {
            Left -> West
            Right -> East
        }

    override fun toString() = "North"
}
object South : Compass() {
    override fun turn(hand: Hand) =
        when (hand) {
            Left -> East
            Right -> West
        }

    override fun toString() = "South"
}
object East : Compass() {
    override fun turn(hand: Hand) =
        when (hand) {
            Left -> North
            Right -> South
        }

    override fun toString() = "East"
}
object West : Compass() {
    override fun turn(hand: Hand) =
        when (hand) {
            Left -> South
            Right -> North
        }

    override fun toString() = "West"
}


sealed class Hand {}
object Left: Hand() {}
object Right: Hand() {}

val regex = Regex("(\\w)(\\d+)")
fun String.toLetterAndNumber(): Pair<String, Int> {
    val (char, distance) = regex.matchEntire(this)?.destructured?: throw RuntimeException(this)
    return char to distance.toInt()
}