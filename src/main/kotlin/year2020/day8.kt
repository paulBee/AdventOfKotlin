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

    Runner(instructions).run(initialState).also { displayPart1(it.left!!) }

    CorrectingRunner(instructions).run(initialState).also { displayPart2(it.right!!) }
}

data class State(val pointer: Int, val acc: Int, val previous: State?) {

    fun next(operation: Operation) =
        State(
            this.pointer + operation.pointerChange(),
            this.acc + operation.accChange(),
            this
        )

    fun hasVisited(pointer: Int): Boolean = previous?.pointer == pointer || previous?.hasVisited(pointer) ?: false
}

class Runner(val operations: List<Operation>) {

    tailrec fun run(state: State): Either<Int, Int> =
        when {
            state.hasVisited(state.pointer) -> Left(state.acc)
            operations.size == state.pointer -> Right(state.acc)
            else -> run(state.next(operations[state.pointer]))
        }
}

class CorrectingRunner(val operations: List<Operation>) {
    val runner = Runner(operations)

    tailrec fun run(state: State): Either<Int, Int> =
        when(val operation = operations[state.pointer]) {
            is Accumulate -> Left(0)
            is Jump -> runner.run(state.next(operation.asNoOp()))
            is NoOp -> runner.run(state.next(operation.asJump()))
        }
            .asRight() ?: run(state.next(operations[state.pointer]))
}

sealed class Operation {
    open fun pointerChange() = 1
    open fun accChange() = 0
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