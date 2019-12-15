import coOrdinates.Coordinate
import intcodeComputers.InputOutputComputer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import kotlin.math.ceil
import java.lang.RuntimeException

const val North = 1L
const val South = 2L
const val East = 3L
const val West = 4L


@ExperimentalCoroutinesApi
suspend fun main() {
    val programInstructions = readProgramInstructions("day15.txt")
    val inputOutputComputer = InputOutputComputer(programInstructions)

    var currentPosition = Coordinate(0,0)
    val maze = mutableMapOf<Coordinate, String>()
    withContext(Dispatchers.Default) {
        inputOutputComputer.start()

        //this is pretty naff, Its quite reliable but Im going to really struggle in bigger mazes
        repeat(2000000) {
            val tryDirection = pickRandomDirection()
            val robotFound = tryMove(tryDirection, inputOutputComputer)
            val exploredPosition = move(tryDirection, currentPosition)

            maze[exploredPosition] = robotFound.pixelString()

            if (!robotFound.isWall()) {
                currentPosition = exploredPosition
            }
        }
        println("Lets look at the maze")
        printMap(maze)

        val findingMaze = HashMap(maze)
        println("Oxygen can be reached in ${countStepsToOxygen(findingMaze)} steps")
        printMap(findingMaze)

        val fillingMaze = HashMap(maze)
        println("Oxygen will fill the tank in ${timeToFillTank(fillingMaze)} minutes")
        printMap(fillingMaze)
    }
}

fun timeToFillTank(maze: MutableMap<Coordinate, String>) =
    nextStep(
        maze.filter { it.value == "END" }.map { it.key },
        maze,
        1
    ) { coords -> coords.none { maze[it] == "   " } }
        .let { it - 1 } // because we are finding the step after its finished filling

fun countStepsToOxygen(maze: MutableMap<Coordinate, String>) =
    nextStep(
        listOf(Coordinate(0, 0)),
        maze,
        1
    ) { coords -> coords.any { maze[it] == "END" } }

private tailrec fun nextStep(
    latestCoords: List<Coordinate>,
    maze: MutableMap<Coordinate, String>,
    step: Int,
    endPredicate: (List<Coordinate>) -> Boolean
): Int {

    val adjacent = latestCoords.flatMap { it.allAdjacent() }
    if (endPredicate.invoke(adjacent)) {
        return step
    }
    val nextCoords = adjacent
        .filter { maze[it] == "   " }

    nextCoords.forEach { maze[it] = step.toString().padStart(3, ' ') }
    return nextStep(nextCoords, maze, step + 1, endPredicate)

}

fun printMap(knownPixels: MutableMap<Coordinate, String>) {
    val xPixels = knownPixels.map { it.key.x }
    val yPixels = knownPixels.map { it.key.y }

    val minx = xPixels.min()?: throw RuntimeException(" no pixels")
    val maxx = xPixels.max()?: throw RuntimeException(" no pixels")
    val miny = yPixels.min()?: throw RuntimeException(" no pixels")
    val maxy = yPixels.max()?: throw RuntimeException(" no pixels")

    (miny..maxy).forEach { y ->
        println((minx..maxx)
            .map { x -> if (x==0 && y==0) "STR" else knownPixels.getOrDefault(Coordinate(x, y), "###") }
            .joinToString(""))
    }
}

fun pickRandomDirection() = ceil(Math.random() * 4).toLong()


private fun Long.pixelString() =
    when(this) {
        0L -> "###"
        1L -> "   "
        2L -> "END"
        else -> throw RuntimeException("Dang!")
    }

private fun Long.isWall() = this == 0L

suspend fun tryMove(currentDirection: Long, inputOutputComputer: InputOutputComputer) =
   inputOutputComputer.getOutputForInput(currentDirection)[0]


fun move(direction: Long, currentPosition: Coordinate) =
    when (direction) {
        North -> Coordinate(currentPosition.x, currentPosition.y + 1)
        South -> Coordinate(currentPosition.x, currentPosition.y - 1)
        East -> Coordinate(currentPosition.x + 1, currentPosition.y)
        West -> Coordinate(currentPosition.x - 1, currentPosition.y)
        else -> throw RuntimeException("Boooo!")
    }