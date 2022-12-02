package year2022

import utils.aoc.readLinesFromFile
import java.lang.RuntimeException

fun main() {

    val input = readLinesFromFile("2022/day2.txt")

    input.sumOf{ line -> score(line, ::chooseMoveStrategy) }
        .also(::println)

    input.sumOf { line -> score(line, ::chooseResultStrategy) }
        .also(::println)
}

fun chooseMoveStrategy(instruction: String, elfMove: RPSMove) = instruction.toMove()
fun chooseResultStrategy(instruction: String, elfMove: RPSMove) = instruction.toResult().pickMove(elfMove)

fun score(line: String, strategy: (instruction: String, elfMove: RPSMove) -> RPSMove): Int {

    val (first, second) = line.split(" ")
    val elfMove = first.toMove()
    val myMove = strategy(second, elfMove)

    return myMove.play(elfMove).score + myMove.score

}

enum class RPSMove(val score: Int) {
    Rock(1),
    Paper(2),
    Scissors(3);

    fun play(other: RPSMove): Result =
        when (other) {
            this.previous() -> Result.WIN
            this.next() -> Result.LOSE
            else -> Result.DRAW
        }
}

fun RPSMove.previous() = when(this) {
    RPSMove.Rock -> RPSMove.Scissors
    RPSMove.Paper -> RPSMove.Rock
    RPSMove.Scissors -> RPSMove.Paper
}

fun RPSMove.next() = when(this) {
    RPSMove.Rock -> RPSMove.Paper
    RPSMove.Paper -> RPSMove.Scissors
    RPSMove.Scissors -> RPSMove.Rock
}

enum class Result(val score: Int) {
    WIN(6),
    LOSE(0),
    DRAW(3);

    fun pickMove(opponentMove: RPSMove) = when(this) {
        WIN -> opponentMove.next()
        DRAW -> opponentMove
        LOSE -> opponentMove.previous()
    }
}

fun String.toMove() = when(this) {
    "A" -> RPSMove.Rock
    "X" -> RPSMove.Rock
    "B" -> RPSMove.Paper
    "Y" -> RPSMove.Paper
    "C" -> RPSMove.Scissors
    "Z" -> RPSMove.Scissors
    else -> throw RuntimeException("$this is not a valid move")
}

fun String.toResult(): Result = when(this) {
    "X" -> Result.LOSE
    "Y" -> Result.DRAW
    "Z" -> Result.WIN
    else -> throw RuntimeException("$this is not a valid result")
}