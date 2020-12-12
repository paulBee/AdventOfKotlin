package year2020

import pow
import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.pickRandom
import utils.aoc.readLinesFromFile
import utils.collections.bifurcate
import utils.collections.productOf
import utils.navigation.Coordinate
import utils.strings.splitAtIndex
import utils.strings.toIntUsingDigitsOf
import java.lang.Exception
import kotlin.random.Random.Default.nextInt

fun main() {
    val input = readLinesFromFile("2020/day5.txt")

    val sortedIds = input
        .map {
            pickRandom(
                ::parseTheParcel,
                ::aShiftySolution,
                ::hideAndSeek
            )(it)
        }
        .sorted().also { displayPart1(it.last()) }

    sortedIds
        .zipWithNext()
        .first { (first, second) -> second - first == 2 }
        .also { displayPart2(it.first + 1)}


    aeroplaneSimulation()
}



fun aShiftySolution(boarding: String) =
    boarding.fold(0)
        { acc, c ->
            (acc shl 1).let {
                when(c) {
                    'B' -> it + 1
                    'R' -> it + 1
                    else -> it
                }
            }
        }



fun parseTheParcel(boarding: String) = boarding.splitAtIndex(7)
    .let {
        (row, seat) -> boardingId(rowParser(row), seatParser(seat))
    }

fun boardingId(row: Int, seat: Int) = row * 8 + seat

val rowParser = toIntUsingDigitsOf('F', 'B')
val seatParser = toIntUsingDigitsOf('L', 'R')



fun hideAndSeek (boarding: String) = boarding.splitAtIndex(7)
    .let {
        (row, seat) -> boardingId(
            followDirectionsToNumber(row, 'F', 'B'),
            followDirectionsToNumber(seat, 'L', 'R')
        )
    }

fun followDirectionsToNumber (directions: String, lower: Char, upper: Char) =
    directions.fold(
        (0 until (2 pow directions.length)).toList())
        { acc, next ->
            when (next) {
                upper -> acc.bifurcate().second
                lower -> acc.bifurcate().first
                else -> throw RuntimeException("I'm sorry, I cant do that Dave")
            }
        }
        .let {
            assert(it.size == 1)
            it[0]
        }

fun aeroplaneSimulation() {
    val terminal = Terminal()
    val engineer = terminal.hireEngineer()
    val aeroplane = engineer.constructResizablePlane()

    terminal.shuttlePassengersToRunway().forEach{ passenger -> passenger.stepInto(aeroplane) }

    engineer assembleWallsOf aeroplane
    engineer runDiagnostics aeroplane

    try {
        aeroplane.takeOff()
    } catch (e: InEfficientFuelUseException) {
        val seat = aeroplane.findUnusedSeat()
        val missingBoardingPass = terminal.printNewBoardingPassFor(seat)
        aeroplane.reuniteBoardingPassAndPassenger(missingBoardingPass)
        aeroplane.takeOff()
    } catch (e: RecklessUseOfMachineryException) {
        println("logging error to make it look like Im taking this error seriously")
        throw e // throwing that hot potato for the lawyers to handle
    }
}

interface Location {
    fun welcome(visitor: Visitor)
}

interface WeightBearing {
    var strainingUnder: WearyLoad?

    fun takeWeightOf (load: WearyLoad) {
        if (strainingUnder != null) {
            val heavyLoad = strainingUnder
            when (heavyLoad) {
                is AmenableToBeMoved -> heavyLoad.relinquish()  // how do I organise this so the extra cast isnt needed?
                else -> throw RuntimeException("How embarrassing! a fight commences between $heavyLoad and $load")
            }
        }
        this.strainingUnder = load
    }

    fun isTaken() = strainingUnder != null
}

interface WearyLoad {

    var currentlyOn: WeightBearing?

    fun lowerOnTo(seat: WeightBearing) {
        seat.takeWeightOf(this)
        currentlyOn = seat
    }
}

interface Savvy {
    fun translateBoardingPassToCoord(boardingPass: String): Coordinate =
        boardingPass.splitAtIndex(7).let { (rowPart, seatPart) -> Coordinate(rowParser(rowPart), seatParser(seatPart)) }
}

interface Visitor {

    fun stepInto(airplane: Location) {
        airplane.welcome(this)
    }
}

interface BoardingPassOwner {
    fun presentBoardingPass(): String
}

interface Flyer: WearyLoad, Visitor, BoardingPassOwner

interface SeatingArea {
    fun seatAt(coord: Coordinate): Seat?
}

interface OrderedSeatingArea: SeatingArea {
    fun seatsFromStart() : Sequence<Seat>
    fun seatsFromEnd() : Sequence<Seat>
}

interface Usher {
    fun offerSeatingAssistance(flyer: Flyer)
}

class Aeroplane(length: Int, width: Int): Location, OrderedSeatingArea {

    private val seats = productOf(0 until length, 0 until width)
        .associate { (colNum, seatNum) -> Coordinate(colNum, seatNum) to Seat(colNum, seatNum) }

    private val orderedSeats = seats.entries
        .sortedWith(compareBy({ it.key.x }, { it.key.y } ))
        .map { it.value }

    private var hasWalls = false

    var naughtyCorner: Visitor? = null

    override fun seatsFromStart(): Sequence<Seat> = orderedSeats.asSequence().filter { !it.isDismantled }

    override fun seatsFromEnd(): Sequence<Seat> = orderedSeats.asReversed().asSequence().filter { !it.isDismantled }

    override fun seatAt(coord: Coordinate) = seats[coord]

    override fun welcome(visitor: Visitor) {
        when (visitor) {
            !is BoardingPassOwner -> summonSteward().putInTheNaughtyCorner(visitor)
            is FrequentFlyer -> visitor.takeSeatIn(this)
            is AnxiousAviator -> summonSteward().offerSeatingAssistance(visitor)
        }
    }

    private fun summonSteward() = Steward(this)

    fun constructWalls() {
        hasWalls = true
    }

    fun allSeatsUsed() = seats.values.all { it.isTaken() || it.isDismantled }

    fun fly() = println("We're walking in the air")

    fun takeOff() {
        when {
            !hasWalls -> throw RecklessUseOfMachineryException("This is why we should decide how many seats to have BEFORE printing the tickets")
            !allSeatsUsed() -> throw InEfficientFuelUseException("Show some respect to mother earth")
            else -> fly()
        }
    }

    fun findUnusedSeat(): Seat = seatsFromStart().first { !it.isTaken() && !it.isDismantled }

    fun reuniteBoardingPassAndPassenger(missingBoardingPass: String) {
        getProtagonistFromCorner().retrieveBoardingPassAndSit(missingBoardingPass, this)
    }

    private fun getProtagonistFromCorner(): TheProtagonist {
        return naughtyCorner as? TheProtagonist ?: throw RuntimeException("The world doesnt revolve around the supporting cast")
    }

}

class Terminal () {
    fun shuttlePassengersToRunway(): List<Visitor> = readLinesFromFile("2020/day5.txt")
        .map { pickRandom(::AnxiousAviator, ::FrequentFlyer)(it) }
        .plus(TheProtagonist())
        .shuffled()

    fun hireEngineer() = Engineer()
    fun printNewBoardingPassFor(seat: Seat) =
        seat.id.toString(2).padStart(10, '0').splitAtIndex(7)
            .let{ (row, seat) ->
                row.replace('0', 'F')
                    .replace('1', 'B') +
                seat.replace('0', 'L')
                    .replace('1', 'R')
            }.also { println("re-issued $it for seat ${seat.id}") }

}

class InEfficientFuelUseException(message: String): Exception(message)
class RecklessUseOfMachineryException(message: String): Exception(message)

class Steward(val plane: Aeroplane): Savvy, Usher, WearyLoad {

    override var currentlyOn: WeightBearing? = null

    override fun offerSeatingAssistance(flyer: Flyer) {
        flyer.presentBoardingPass()
            .let { boardingPass -> translateBoardingPassToCoord(boardingPass) }
            .let { coords -> plane.seatAt(coords) ?: throw RuntimeException("The only thing I can find is my P45") }
            .also { seat -> flyer.lowerOnTo(seat) }
    }

    fun putInTheNaughtyCorner(visitor: Visitor) {
        chastise(visitor)
        plane.naughtyCorner = visitor
    }

    private fun chastise(visitor: Visitor) {
        when (visitor) {
            is TheProtagonist -> println("If you spent less time writing a quick camera program, and more time holding on to documents...")
            else -> println("No words... just no words")
        }
    }
}

class Engineer() {

    fun constructResizablePlane() = Aeroplane(128, 8)

    infix fun assembleWallsOf(aeroplane: Aeroplane) {
        aeroplane.seatsFromStart()
            .takeWhile { !it.isTaken() }
            .forEach { it.dismantle() }

        aeroplane.seatsFromEnd()
            .takeWhile { !it.isTaken() }
            .forEach { it.dismantle() }

        aeroplane.constructWalls()
    }

    infix fun runDiagnostics(aeroplane: Aeroplane) {
        val minId = aeroplane.seatsFromStart().minOf { it.id }
        val maxId = aeroplane.seatsFromStart().maxOf { it.id }
        val count = aeroplane.seatsFromStart().count()

        println("Plane Assembled with $count seats from $minId to $maxId")
    }
}

class Seat (val colNum: Int, val seatNum: Int): WeightBearing {

    val id: Int by lazy { boardingId(colNum , seatNum) }
    override var strainingUnder: WearyLoad? = null

    var isDismantled = false

    fun dismantle() {

        isDismantled = true
    }

}

class AnxiousAviator (val boardingPass: String): Flyer {

    override var currentlyOn: WeightBearing? = null
    override fun presentBoardingPass() = boardingPass

}

enum class Mood {
    ELF,
    HAPPY,
    NON_PLUSSED,
    ANGRY,
    FURIOUS
}

class FrequentFlyer (val boardingPass: String) : Flyer, Savvy {

    val luggage = when (nextInt(20)) {
        1 -> Luggage(this)
        else -> null
    }
    val mood = pickRandom(Mood.ELF, Mood.HAPPY, Mood.NON_PLUSSED, Mood.ANGRY, Mood.FURIOUS)

    override var currentlyOn: WeightBearing? = null
    override fun presentBoardingPass() = boardingPass

    fun takeSeatIn(area: SeatingArea) {
        val mySeatLocation = translateBoardingPassToCoord(boardingPass)
        val seat = area.seatAt(mySeatLocation) ?: throw RuntimeException("Am I even on this plane?")
        lowerOnTo(seat)
        luggage?.also {
            val possibleLuggageSpot = Coordinate(mySeatLocation.x, mySeatLocation.y + 1)
            area.seatAt(possibleLuggageSpot)?.also { spareSeat -> if (!spareSeat.isTaken()) luggage.lowerOnTo(spareSeat) }
        }
    }

    fun putLuggageOnLap() {
        when (mood) {
            Mood.ELF -> println("Merry Christmas Friend")
            Mood.HAPPY -> println("Want to talk to me the whole way?")
            Mood.NON_PLUSSED -> {}
            Mood.ANGRY -> println("Show me your boarding pass")
            Mood.FURIOUS -> println("aeroplane.summonSteward()... AEROPLANE.SUMMONSTEWARD()... WHY ISNT THIS WORKING!")
        }
    }

}

interface AmenableToBeMoved {
    fun relinquish()
}

class Luggage(val owner: FrequentFlyer): WearyLoad, AmenableToBeMoved {
    override var currentlyOn: WeightBearing? = null
    override fun relinquish() {
        currentlyOn = null
        owner.putLuggageOnLap()
    }

}

class TheProtagonist(): WearyLoad, Visitor, Savvy {

    fun retrieveBoardingPassAndSit(missingBoardingPass: String, seatingArea: SeatingArea) {
        println("Gimme that star")
        seatingArea.seatAt(translateBoardingPassToCoord(missingBoardingPass))?.also { lowerOnTo(it) }
    }

    override var currentlyOn: WeightBearing? = null
}