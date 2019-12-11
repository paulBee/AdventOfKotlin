import coOrdinates.Coordinate
import coOrdinates.DIRECTION
import intcodeComputers.Program
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

@ExperimentalCoroutinesApi
fun main() = runBlocking {

    val programInstructions = readTextFromFile("day11.txt").split(",").map { it.toLong() }

    part1(programInstructions)

    val robot = Robot(programInstructions, hashMapOf(Pair(Coordinate(0,0), COLOUR.WHITE)))
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

@ExperimentalCoroutinesApi
private suspend fun part1(programInstructions: List<Long>) {
    val robot = Robot(programInstructions)
    withContext(Dispatchers.Default) { robot.paint() }
    println(robot.whereHaveYouPainted().keys.size)
}

@ExperimentalCoroutinesApi
class Robot(programInstructions: List<Long>, initialWhite: Map<Coordinate, COLOUR> = HashMap()) {

    private val roboEyes = Channel<Long>()
    private val instructionSource = Channel<Long>(Int.MAX_VALUE)

    private val program = Program(programInstructions, roboEyes, instructionSource)

    private val paintLocations = initialWhite.toMutableMap()
    private var currentLocation = Coordinate(0,0)
    private var currentDirection = DIRECTION.UP

    var inputsSent = 0

    private lateinit var programRun : Deferred<Long>

    suspend fun paint() {
        programRun = GlobalScope.async { program.run() }

        GlobalScope.launch {runRunRobot() }

        programRun.await()

    }

    private suspend fun runRunRobot() {
        while (programRun.isActive || !instructionSource.isEmpty) {
            val robotInstructions = gatherNextInstructions()
            robotInstructions.forEach {
                val (colour, direction) = it
                paintSquare(colour)
                move(direction)
                println("Now at $currentLocation")
            }

            inputsSent++
            roboEyes.send(colourOfCurrentSquare().toLong())

        }
    }

    private fun colourOfCurrentSquare() = paintLocations.getOrDefault(currentLocation, COLOUR.BLACK)

    private fun paintSquare(colour: COLOUR) {
        paintLocations[currentLocation] = colour
    }

    private fun move(direction: DIRECTION) {
        currentDirection = when (currentDirection) {
            DIRECTION.UP -> if (direction == DIRECTION.LEFT) DIRECTION.LEFT else DIRECTION.RIGHT
            DIRECTION.DOWN -> if (direction == DIRECTION.LEFT) DIRECTION.RIGHT else DIRECTION.LEFT
            DIRECTION.LEFT -> if (direction == DIRECTION.LEFT) DIRECTION.DOWN else DIRECTION.UP
            DIRECTION.RIGHT -> if (direction == DIRECTION.LEFT) DIRECTION.UP else DIRECTION.DOWN
        }
        currentLocation = currentLocation.moveDistance(currentDirection, 1)
    }

    private suspend fun gatherNextInstructions(): List<Pair<COLOUR, DIRECTION>> {

        withContext(Dispatchers.Default) {
            while (programRun.isActive && program.inputsRequested == inputsSent ) {
                delay(1)
            }
        }

        val outputs = ArrayList<Long>()
        while (!instructionSource.isEmpty) {
            outputs.add(instructionSource.receive())
        }
        if (outputs.size % 2 != 0) {
            throw RuntimeException("Its as we feared... instructions straddle inputs")
        }

        return outputs.chunked(2)
            .map { Pair(it[0].toColour(), it[1].toDirection()) }
            .also {
                println(it)
            }
    }

    fun whereHaveYouPainted(): Map<Coordinate, COLOUR> = paintLocations

}

enum class COLOUR {
    BLACK, WHITE;

    fun toLong() =
        when (this) {
            BLACK -> 0L
            WHITE -> 1L
        }

    fun displayChar() =
        when (this) {
            BLACK -> "."
            WHITE -> "#"
        }
}

fun Long.toDirection() =
    when (this) {
        0L -> DIRECTION.LEFT
        1L -> DIRECTION.RIGHT
        else -> throw IllegalStateException("$this I cant turn that way. Im just a robot :(")
    }

fun Long.toColour() =
    when (this) {
        0L -> COLOUR.BLACK
        1L -> COLOUR.WHITE
        else -> throw IllegalStateException("$this ent no colour I've ever known")
    }