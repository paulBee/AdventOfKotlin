package year2020

import utils.algorithm.lowestCommonMultiple
import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile

fun main () {
    val (timeInput, bussesInput) = readLinesFromFile("2020/day13.txt")

    val time = timeInput.toLong()

    val busses = bussesInput.toBusses()

    busses
        .map { it.busId }
        .map { busId -> busId to busId - (time % busId) }
        .minByOrNull { it.second }
        ?.also { displayPart1(it.first * it.second) }


    busses.fold(PuzzleState(1L, 1L))
    { (time, synchronisedPeriod), bus ->
        PuzzleState(
            generateSequence(time) { it + synchronisedPeriod }.first { bus.test(it) },
            lowestCommonMultiple(synchronisedPeriod, bus.period)
        )
    }.also { displayPart2(it.time) }

}

data class PuzzleState(val time: Long, val synchronisedPeriod: Long)

data class Bus(val busId: Long, val offset: Long) {
    val period = busId
    fun test (time: Long) = (time + offset) % busId == 0L
}

fun String.toBusses(): List<Bus> =
    this.split(",")
        .foldIndexed(emptyList())
        { offset, busses, input ->
            when (input) {
                "x" -> busses
                else -> busses.plus(Bus(input.toLong(), offset.toLong()))
            }
        }