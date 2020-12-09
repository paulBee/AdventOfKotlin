package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.types.Either
import utils.types.Left
import utils.types.Right

fun main() {

    val instructions = readLinesFromFile("2020/day8.txt").map { parseInstruction(it) }
    val initialState = State(0, 0, null)

    when(val result = runToCompletion(instructions, initialState)) {
        is Left -> displayPart1(result.value)
        is Right -> println("The program unexpectedly completed with ${result.value}")
    }

    when(val result = runWithCorrection(instructions, initialState)) {
        is Left -> println("No correction was found :(")
        is Right -> displayPart2(result.value)
    }
}

data class State(val pointer: Int, val acc: Int, val previous: State?) {
    fun hasVisited(pointer: Int): Boolean = previous?.pointer == pointer || previous?.hasVisited(pointer) ?: false
}

fun runToCompletion(operations: List<Operation>, state: State): Either<Int, Int> =
    when {
        state.hasVisited(state.pointer) -> Left(state.acc)
        operations.size == state.pointer -> Right(state.acc)
        else -> runToCompletion(operations, operations[state.pointer].next(state))
    }

fun runWithCorrection(operations: List<Operation>, state: State): Either<Int, Int> =
    when(val operation = operations[state.pointer]) {
        is Accumulate -> Left(0)
        is Jump -> runToCompletion(operations, operation.asNoOp().next(state))
        is NoOp -> runToCompletion(operations, operation.asJump().next(state))
    }
    .asRight() ?: runWithCorrection(operations, operations[state.pointer].next(state))


sealed class Operation {
    open fun pointerChange() = 1
    open fun accChange() = 0

    fun next(current: State) = State(current.pointer + pointerChange(), current.acc + accChange(), current)
}

class Accumulate(val argument: Int) : Operation() {
    override fun accChange() = argument
}

class Jump(val argument: Int) : Operation() {
    override fun pointerChange() = argument
    fun asNoOp() = NoOp(argument)
}

class NoOp(val argument: Int) : Operation() {
    fun asJump() = Jump(argument)
}

val instructionRegex = "(\\w+) ([+-]\\d+)".toRegex()
fun parseInstruction(string: String): Operation {
    val (code, digit) = instructionRegex.matchEntire(string)?.destructured ?: throw RuntimeException(string)
    val value = digit.toInt()
    return when (code.toLowerCase()) {
        "acc" -> Accumulate(value)
        "jmp" -> Jump(value)
        "nop" -> NoOp(value)
        else -> throw RuntimeException("Unsupported operation $code")
    }
}