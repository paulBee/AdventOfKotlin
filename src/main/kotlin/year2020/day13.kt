package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile

fun main () {
    val (timeInput, bussesInput) = readLinesFromFile("2020/day13.txt")

    val time = timeInput.toLong()

    val bussesAndXs = bussesInput.split(",")

    val busses = bussesAndXs
        .filter { it != "x" }
        .map { it.toLong() }

    busses.map { it to it - (time % it) }.minByOrNull { it.second }
        ?.also { displayPart1(it.first * it.second) }

    val bussesAndOffset = busses.map { it to bussesAndXs.indexOf(it.toString()) }

    var checkingIncrement = 1L
    var currentGuess = 1L


    bussesAndOffset.forEach { (bus, offset) ->
        while ((currentGuess + offset) % bus != 0L) {
            currentGuess += checkingIncrement
        }
        checkingIncrement *= bus // this ensures that the next guess will work for all previously considered busses
    }

    displayPart2(currentGuess)

}