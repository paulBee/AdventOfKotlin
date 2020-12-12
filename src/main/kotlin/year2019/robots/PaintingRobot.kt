package year2019.robots

import year2019.intcodeComputers.Program
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import utils.navigation.*
import year2019.misc.COLOUR

@ExperimentalCoroutinesApi
class PaintingRobot(programInstructions: List<Long>, initialWhite: Map<Coordinate, COLOUR> = HashMap()) {

    private val roboEyes = Channel<Long>()
    private val instructionSource = Channel<Long>(Int.MAX_VALUE)

    private val program = Program(programInstructions, roboEyes, instructionSource)

    private val paintLocations = initialWhite.toMutableMap()
    private var currentLocation = Coordinate(0,0)
    private var currentDirection: Direction = Up

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

    private fun paintAndMove(it: Pair<COLOUR, Rotation>) {
        val (colour, rotation) = it
        paintSquare(colour)
        turnAndMoveForward(rotation)
    }

    private fun colourOfCurrentSquare() = paintLocations.getOrDefault(currentLocation, COLOUR.BLACK)

    private fun paintSquare(colour: COLOUR) {
        paintLocations[currentLocation] = colour
    }

    private fun turnAndMoveForward(rotation: Rotation) {
        currentDirection = currentDirection.rotate(rotation)
        currentLocation = currentLocation.moveDistance(currentDirection, 1)
    }

    private suspend fun gatherNextInstructions(): List<Pair<COLOUR, Rotation>> {

        waitForAnInput()

        return drainOutput()
            .chunked(2)
            .map { Pair(it[0].toColour(), it[1].toRotation()) }
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

    private fun Long.toRotation(): Rotation =
        when (this) {
            0L -> Left
            1L -> Right
            else -> throw IllegalStateException("$this I cant turn that way. Im just a robot :(")
        }

    private fun Long.toColour() =
        when (this) {
            0L -> COLOUR.BLACK
            1L -> COLOUR.WHITE
            else -> throw IllegalStateException("$this ent no colour I've ever known")
        }
}