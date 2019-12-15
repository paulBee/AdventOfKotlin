import coOrdinates.Coordinate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import misc.COLOUR
import robots.PaintingRobot

@ExperimentalCoroutinesApi
fun main() = runBlocking {

    val programInstructions = readProgramInstructions("day11.txt")

    part1(programInstructions)

    part2(programInstructions)
}

@ExperimentalCoroutinesApi
private suspend fun part1(programInstructions: List<Long>) {
    val robot = PaintingRobot(programInstructions)
    withContext(Dispatchers.Default) { robot.paint() }
    val paintMap = robot.whereHaveYouPainted()
    println("Robot visited ${paintMap.keys.size} places")

    printPaint(paintMap)
}

@ExperimentalCoroutinesApi
private suspend fun part2(programInstructions: List<Long>) {
    val robot = PaintingRobot(programInstructions, hashMapOf(Pair(Coordinate(0, 0), COLOUR.WHITE)))
    withContext(Dispatchers.Default) { robot.paint() }
    val paintMap = robot.whereHaveYouPainted()

    printPaint(paintMap)
}

fun printPaint(paintMap: Map<Coordinate, COLOUR>) {
    val xs = paintMap.keys.map { it.x }
    val ys =paintMap.keys.map { it.y }

    val minX = xs.min()?: throw java.lang.IllegalStateException()
    val maxX = xs.max()?: throw java.lang.IllegalStateException()
    val minY = ys.min()?: throw java.lang.IllegalStateException()
    val maxY = ys.max()?: throw java.lang.IllegalStateException()

    (minY..maxY).reversed().forEach {y ->
        val row = (minX..maxX).map { x -> Coordinate(x,y) }
            .map { paintMap.getOrDefault(it, COLOUR.BLACK) }
            .map { it.displayChar() }
            .joinToString("")

        println(row)
    }
}