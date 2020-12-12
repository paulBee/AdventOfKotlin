package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.navigation.*
import utils.strings.toLetterAndNumber

fun main() {

    val navigator = Navigator()

    val directionalBoat = DirectionalBoat(theOrigin, East)
    val waypointBoat = WaypointBoat(theOrigin, Waypoint(10, -1))

    navigator.navigate(directionalBoat).also { displayPart1(it.currentLocation().manhattanDisplacement()) }
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
                    "L" -> boat.navigateByRotation(Left, amount/90)
                    "R" -> boat.navigateByRotation(Right, amount/90)
                    "F" -> boat.forwards(amount)
                    else -> throw RuntimeException("wtf is $letter")
                }
            }
        }
}

interface Boat {
    fun navigateByCompass(compassDirection: Compass, amount: Int)
    fun navigateByRotation(direction: Rotation, amount: Int)
    fun forwards(amount: Int)
    fun currentLocation(): Coordinate
}

typealias Waypoint = Coordinate
class WaypointBoat(private var location: Coordinate, private var waypoint: Waypoint): Boat {

    override fun navigateByCompass(compassDirection: Compass, amount: Int)  {
        waypoint = compassDirection.travel(waypoint, amount)
    }

    override fun navigateByRotation(direction: Rotation, amount: Int) {
        waypoint = generateSequence(waypoint) { it.rotate(direction) }.elementAt(amount)
    }

    override fun forwards(amount: Int) {
        location = generateSequence(location) { it.move(waypoint.x, waypoint.y) }.elementAt(amount)
    }

    override fun currentLocation() = location

}

class DirectionalBoat(private var location: Coordinate, private var facing : Compass): Boat {

    override fun navigateByCompass(compassDirection: Compass, amount: Int) {
        location = compassDirection.travel(location, amount)
    }

    override fun navigateByRotation(direction: Rotation, amount: Int) {
        facing = generateSequence(facing) { it.rotate(direction) }.elementAt(amount)
    }

    override fun forwards(amount: Int) = navigateByCompass(facing, amount)

    override fun currentLocation() = location

}

typealias Instruction = Pair<String, Int>