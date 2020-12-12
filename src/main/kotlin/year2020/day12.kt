package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import kotlin.math.absoluteValue

fun main() {

    val navigator = Navigator()

    val simpleBoat = DirectionalBoat(origin, East)
    val waypointBoat = WaypointBoat(origin, Waypoint(10, -1))

    navigator.navigate(simpleBoat).also { displayPart1(it.currentLocation().manhattanDisplacement()) }
    navigator.navigate(waypointBoat).also { displayPart2(it.currentLocation().manhattanDisplacement()) }
}

class Navigator {

    private val instructions: List<Instruction> = readLinesFromFile("2020/day12.txt").map { it.toLetterAndNumber() }

    fun <T: Boat> navigate(boat: T): T =
        boat.also {
            instructions.forEach { (letter, amount) ->
                when (letter) {
                    "N" -> boat.navigateByCompass(North, amount)
                    "S" -> boat.navigateByCompass(South, amount)
                    "E" -> boat.navigateByCompass(East, amount)
                    "W" -> boat.navigateByCompass(West, amount)
                    "L" -> boat.navigateByRotation(Rotate.Left, amount/90)
                    "R" -> boat.navigateByRotation(Rotate.Right, amount/90)
                    "F" -> boat.forwards(amount)
                    else -> throw RuntimeException("wtf is $letter")
                }
            }
        }

}

interface Boat {
    fun navigateByCompass(compassDirection: Compass, amount: Int)
    fun navigateByRotation(direction: Rotate, amount: Int)
    fun forwards(amount: Int)
    fun currentLocation(): Location2D
}

typealias Waypoint = Location2D
class WaypointBoat(private var location: Location2D, private var waypoint: Waypoint): Boat {

    override fun navigateByCompass(compassDirection: Compass, amount: Int)  {
        waypoint = waypoint.move(compassDirection, amount)
    }

    override fun navigateByRotation(direction: Rotate, amount: Int) {
        waypoint = generateSequence(waypoint) { it.rotate(direction) }.elementAt(amount)
    }

    override fun forwards(amount: Int) {
        location = generateSequence(location) { it.move(waypoint.x, waypoint.y) }.elementAt(amount)
    }

    override fun currentLocation() = location

}

class DirectionalBoat(private var location: Location2D, private var facing : Compass): Boat {

    override fun navigateByCompass(compassDirection: Compass, amount: Int) {
        location = location.move(compassDirection, amount)
    }

    override fun navigateByRotation(direction: Rotate, amount: Int) {
        facing = generateSequence(facing) { it.turn(direction) }.elementAt(amount)
    }

    override fun forwards(amount: Int) = navigateByCompass(facing, amount)

    override fun currentLocation() = location

}

val origin = Location2D(0, 0)

class Location2D(val x: Int, val y: Int) {
    fun move(compassDirection: Compass, length: Int) =
        when (compassDirection) {
            North -> Location2D(x, y - length)
            South -> Location2D(x, y + length)
            East -> Location2D(x + length, y)
            West -> Location2D(x - length, y)
        }

    fun move(dx: Int, dy: Int) = Location2D(x + dx, y + dy)

    fun rotate(rotate: Rotate) =
        when (rotate) {
            Rotate.Left -> Location2D(y, -x)
            Rotate.Right -> Location2D(-y, x)
        }

    fun manhattanDisplacement() = x.absoluteValue + y.absoluteValue
}

sealed class Compass {
    abstract fun turn(rotate: Rotate): Compass
}
object North : Compass() {
    override fun turn(rotate: Rotate) =
        when (rotate) {
            Rotate.Left -> West
            Rotate.Right -> East
        }

    override fun toString() = "North"
}
object South : Compass() {
    override fun turn(rotate: Rotate) =
        when (rotate) {
            Rotate.Left -> East
            Rotate.Right -> West
        }

    override fun toString() = "South"
}
object East : Compass() {
    override fun turn(rotate: Rotate) =
        when (rotate) {
            Rotate.Left -> North
            Rotate.Right -> South
        }

    override fun toString() = "East"
}
object West : Compass() {
    override fun turn(rotate: Rotate) =
        when (rotate) {
            Rotate.Left -> South
            Rotate.Right -> North
        }

    override fun toString() = "West"
}


sealed class Rotate {
    object Left: Rotate() {}
    object Right: Rotate() {}
}

typealias Instruction = Pair<String, Int>

val regex = Regex("(\\w)(\\d+)")
fun String.toLetterAndNumber(): Pair<String, Int> {
    val (letter, distance) = regex.matchEntire(this)?.destructured?: throw RuntimeException(this)
    return letter to distance.toInt()
}