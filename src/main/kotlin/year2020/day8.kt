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

    fun next(instruction: Instruction): State = instruction.operation.next(this, instruction.argument)

    fun hasVisited(pointer: Int): Boolean = previous?.pointer == pointer || previous?.hasVisited(pointer) ?: false
}

fun runToCompletion(instructions: List<Instruction>, state: State): Either<Int, Int> =
    when {
        state.hasVisited(state.pointer) -> Left(state.acc)
        instructions.size == state.pointer -> Right(state.acc)
        else -> runToCompletion(instructions, state.next(instructions[state.pointer]))
    }

fun runWithCorrection(instructions: List<Instruction>, state: State): Either<Int, Int> =
    instructions[state.pointer].let {
        when(it.operation) {
            is Accumulate -> runWithCorrection(instructions, state.next(it))
            is Jump -> when (val result = runToCompletion(instructions, state.next(Instruction(NoOp, it.argument)))) {
                is Left -> runWithCorrection(instructions, state.next(it))
                is Right -> result
            }
            is NoOp -> when (val result = runToCompletion(instructions, state.next(Instruction(Jump, it.argument)))) {
                is Left -> runWithCorrection(instructions, state.next(it))
                is Right -> result
            }
        }
    }

sealed class Operation {
    abstract fun next(current: State, argument: Int): State
}

object Accumulate : Operation() {
    override fun next(current: State, argument: Int) =
        State(current.pointer + 1, current.acc + argument, current)
}

object Jump : Operation() {
    override fun next(current: State, argument: Int) =
        State(current.pointer + argument, current.acc, current)
}

object NoOp : Operation() {
    override fun next(current: State, argument: Int) =
        State(current.pointer + 1, current.acc, current)
}

data class Instruction(val operation: Operation, val argument: Int) {}
val instructionRegex = "(\\w+) ([+-]\\d+)".toRegex()
fun parseInstruction(string: String): Instruction {
    val (code, digit) = instructionRegex.matchEntire(string)?.destructured ?: throw RuntimeException(string)
    val value = digit.toInt()
    return when (code.toLowerCase()) {
        "acc" -> Instruction(Accumulate, value)
        "jmp" -> Instruction(Jump, value)
        "nop" -> Instruction(NoOp, value)
        else -> throw RuntimeException("Unsupported operation $code")
    }
}