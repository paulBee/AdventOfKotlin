package year2022

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import utils.aoc.readLinesFromFile
import utils.navigation.Coordinate
import java.util.function.Predicate

fun main() {

    val locations = readLinesFromFile("2022/day12.txt").toLocations()

    val startLocation = locations.values.first { it.isStart }
    val endLocation = locations.values.first { it.isEnd }

    val upwardsPathFinder = shortestPath(locations.upwardsTraversal())
    val downwardsPathFinder = shortestPath(locations.downwardsTraversal())

    upwardsPathFinder(startLocation, Location::isBestTransmissionSpot).also { println(it.size - 1) }
    downwardsPathFinder(endLocation, Location::isLowestPoint).also { println(it.size - 1) }


    // thought I'd work through a* for some practice
    astar(locations)?.also { println(it.size - 1) }

}

fun Location.isBestTransmissionSpot() = this.isEnd
fun Location.isLowestPoint() = this.elevation == 'a'.toInt()

fun Map<Coordinate, Location>.upwardsTraversal(): ValidTraversals = { location ->
    location.coordinate.orthognals
        .mapNotNull { this[it] }
        .filter { adjLocation -> adjLocation.elevation - location.elevation <= 1  }
}

fun Map<Coordinate, Location>.downwardsTraversal(): ValidTraversals = { location ->
    location.coordinate.orthognals
        .mapNotNull { this[it] }
        .filter { adjLocation -> location.elevation - adjLocation.elevation <= 1  }
}

fun shortestPath(allowableSteps: ValidTraversals): ShortestPathFromTo =
    { startLocation, targetCheck ->
        generateSequence(listOf(persistentListOf(startLocation))) { allPaths ->
            val visited = allPaths.flatten().toSet()
            allPaths.flatMap { path ->
                allowableSteps(path.currentLocation())
                    .filter { !visited.contains(it) }
                    .map { path.add(it) }
            }.eliminateEqualCurrentLocations()
        }.firstPathThatHasReached(targetCheck)
    }

fun Sequence<List<Path>>.firstPathThatHasReached(targetCheck: Predicate<Location>) = this.mapNotNull { paths ->
    paths.firstOrNull { targetCheck.test(it.currentLocation()) }
}.first()

fun List<Path>.eliminateEqualCurrentLocations() = this.distinctBy { it.currentLocation() }

private fun Path.currentLocation() = this.last()

private fun List<String>.toLocations() =  this
    .flatMapIndexed { y, line ->
        line.mapIndexed { x, char ->
            Location(
                Coordinate(x, y),
                char.toElevation(),
                char == 'S',
                char == 'E'
            )
        }
    }.associateBy { it.coordinate }

private fun Char.toElevation() = when(this) {
    'S' -> 'a'.toInt()
    'E' -> 'z'.toInt()
    else -> this.toInt()
}

data class Location(val coordinate: Coordinate, val elevation: Int, val isStart: Boolean, val isEnd: Boolean)

typealias ShortestPathFromTo = (startLocation: Location, targetCheck: (Location) -> Boolean) -> Path
typealias ValidTraversals = (location: Location) -> List<Location>
typealias Path = PersistentList<Location>




// a*

fun astar(locations: Map<Coordinate, Location>): List<Location>? {
    val startLocation = locations.values.first { it.isStart }
    val endLocation = locations.values.first { it.isEnd }

    val neighbours = locations.upwardsTraversal()

    val openList = mutableListOf(AStar(startLocation, null, endLocation.heuristic(startLocation)))
    val closedList = mutableListOf<AStar>()

    while (openList.isNotEmpty()){

        openList.sortBy { it.cost() }
        val currentNode = openList.removeFirst()
        closedList.add(currentNode)

        neighbours(currentNode.location)
            .filter { n -> closedList.none { it.location == n }}
            .forEach { neighbour ->
                if (neighbour.isEnd) {
                    return currentNode.toPath() + neighbour
                }
                val previous = openList.firstOrNull { it.location == neighbour }
                if (previous == null) {
                    openList.add(AStar(neighbour, currentNode, endLocation.heuristic(startLocation)))
                } else if (previous.distance() > currentNode.distance() + 1) {
                    previous.parent = currentNode
                }
            }
    }

    return null
}

private fun Location.heuristic(location: Location) = this.coordinate.manhattanDistanceTo(location.coordinate)

data class AStar(
    val location: Location,
    var parent: AStar?,
    val heuristic: Int,
) {
    fun distance(): Int =  1 + (parent?.distance() ?: -1)
    fun cost() = distance() + heuristic
    fun toPath(): List<Location> = listOf(location) + (parent?.toPath() ?: emptyList())
}