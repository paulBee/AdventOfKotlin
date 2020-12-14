package year2020

import utils.algorithm.lowestCommonMultiple
import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.multiply
import utils.collections.sumLongBy

fun main () {
    val (timeInput, bussesInput) = readLinesFromFile("2020/day13.txt")

    val time = timeInput.toLong()
    val busses = bussesInput.toBusses()

    busses
        .map { bus -> bus to bus.period - (time % bus.period) }
        .minByOrNull { it.second }
        ?.also { displayPart1(it.first.busId * it.second) }


    busses.fold(PuzzleState(1L, 1L))
    { (time, synchronisedPeriod), bus ->
        PuzzleState(
            generateSequence(time) { it + synchronisedPeriod }.first { bus.test(it) },
            lowestCommonMultiple(synchronisedPeriod, bus.period)
        )
    }.also { displayPart2(it.time) }

    displayPart2(chineseRemainderTheorem(busses))
}

data class PuzzleState(val time: Long, val synchronisedPeriod: Long)

data class Bus(val busId: Long, val offset: Long) {
    val period = busId
    val remainder = if (offset == 0L) 0L else period - (period % busId)
    fun test (time: Long) = time  % busId == remainder
}

fun chineseRemainderTheorem(busses: List<Bus>): Long {
    val productOfPeriods = busses.map { it.period }.multiply()

    return busses.sumLongBy { bus ->
        val remainder = bus.remainder
        val partialProduct = productOfPeriods/bus.period
        val thatExtraBitIHaventGrokked =                        // lol. I get this is partialProduct.x ≡ 1 mod (bus.period)
            generateSequence(0L) { it + partialProduct }    // I just havent understood WHY its that!
                .indexOfFirst { it % bus.period == 1L }         // answers on a postcard please :)

        remainder * partialProduct * thatExtraBitIHaventGrokked
    }.let { it % productOfPeriods }
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