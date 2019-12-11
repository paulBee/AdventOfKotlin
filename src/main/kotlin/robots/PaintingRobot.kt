package robots

import coOrdinates.Coordinate
import coOrdinates.DIRECTION
import intcodeComputers.Program
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import misc.COLOUR

@ExperimentalCoroutinesApi
class PaintingRobot(programInstructions: List<Long>, initialWhite: Map<Coordinate, COLOUR> = HashMap()) {

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
            gatherNextInstructions().forEach(::paintAndMove)
            sendColourToInput()
        }
    }

    private suspend fun sendColourToInput() {
        inputsSent++
        roboEyes.send(colourOfCurrentSquare().toLong())
    }

    private fun paintAndMove(it: Pair<COLOUR, DIRECTION>) {
        val (colour, direction) = it
        paintSquare(colour)
        turnAndMoveForward(direction)
    }

    private fun colourOfCurrentSquare() = paintLocations.getOrDefault(currentLocation, COLOUR.BLACK)

    private fun paintSquare(colour: COLOUR) {
        paintLocations[currentLocation] = colour
    }

    private fun turnAndMoveForward(direction: DIRECTION) {
        currentDirection = currentDirection.turn(direction)
        currentLocation = currentLocation.moveDistance(currentDirection, 1)
    }

    private suspend fun gatherNextInstructions(): List<Pair<COLOUR, DIRECTION>> {

        waitForAnInput()

        return drainOutput()
            .chunked(2)
            .map { Pair(it[0].toColour(), it[1].toDirection()) }
    }

    private suspend fun drainOutput(): ArrayList<Long> {
        // PRs very welcome for a cleaner way to do this
        val outputs = ArrayList<Long>()
        while (!instructionSource.isEmpty) {
            outputs.add(instructionSource.receive())
        }
        if (outputs.size % 2 != 0) {
            throw RuntimeException("Its as we feared... instructions straddle inputs")
        }
        return outputs
    }

    private suspend fun waitForAnInput() {
        withContext(Dispatchers.Default) {
            while (programRun.isActive && program.inputsRequested == inputsSent) {
                delay(1)
            }
        }
    }

    fun whereHaveYouPainted(): Map<Coordinate, COLOUR> = paintLocations

    private fun Long.toDirection() =
        when (this) {
            0L -> DIRECTION.LEFT
            1L -> DIRECTION.RIGHT
            else -> throw IllegalStateException("$this I cant turn that way. Im just a robot :(")
        }

    private fun Long.toColour() =
        when (this) {
            0L -> COLOUR.BLACK
            1L -> COLOUR.WHITE
            else -> throw IllegalStateException("$this ent no colour I've ever known")
        }
}