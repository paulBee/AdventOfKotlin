package intcodeComputers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel

val defaultInput = Channel<Int>()
val defualtOutput = Channel<Int>()

@ExperimentalCoroutinesApi
class Program(private val workingMemory: MutableList<Int>, private val input: Channel<Int> = defaultInput, private val output: Channel<Int> = defualtOutput, private val name: String = "program") {

    private var instructionPointer = 0
    private var lastOutput = 0

    suspend fun run(): Int {

            while (!isTerminated()) {
                runInstruction()
            }
            println("$name is finished")
            return getReturnValue()
        }

    suspend fun runInstruction() {
        val nextInstruction = extractInstruction(instructionPointer)
//        println("$name : $nextInstruction")
        performAction(nextInstruction)
        instructionPointer = newPointer(instructionPointer, nextInstruction)
    }

    fun extractInstruction(pointer: Int): Instruction {
        val opcodeInfo = workingMemory[pointer].toString()
        val opcode = opcodeInfo.takeLast(2).toInt().toOpcode()
        val parameterModes = opcodeInfo
            .dropLast(2)
            .reversed()
            .map { it.toString().toInt() }

        val immediateParameters = (instructionPointer..instructionPointer + opcode.parameterSize()).drop(1)
            .map { workingMemory[it] }


        return Instruction(opcode, immediateParameters, parameterModes)
    }

    suspend fun performAction(instruction: Instruction) {
        when (instruction.opcode) {
            OPCODE.ADD -> {
                val number1 = instruction.modeAwareParam(0, workingMemory)
                val number2 = instruction.modeAwareParam(1, workingMemory)
                val saveIndex = instruction.parameters[2]
                workingMemory[saveIndex] = number1 + number2
            }
            OPCODE.MULTIPLY -> {
                val number1 = instruction.modeAwareParam(0, workingMemory)
                val number2 = instruction.modeAwareParam(1, workingMemory)
                val saveIndex = instruction.parameters[2]
                workingMemory[saveIndex] = number1 * number2
            }
            OPCODE.TAKE_INPUT -> {
                val (saveIndex) = instruction.parameters
                workingMemory[saveIndex] = input.receive()
            }
            OPCODE.SEND_OUTPUT -> {
                val outputVal = instruction.modeAwareParam(0, workingMemory)
                lastOutput = outputVal
                output.send(outputVal)
            }
            OPCODE.JUMP_IF_TRUE -> {
                if (instruction.modeAwareParam(0, workingMemory) != 0) {
                    instructionPointer = instruction.modeAwareParam(1, workingMemory)
                }
            }
            OPCODE.JUMP_IF_FALSE -> {
                if (instruction.modeAwareParam(0, workingMemory) == 0) {
                    instructionPointer = instruction.modeAwareParam(1, workingMemory)
                }
            }
            OPCODE.LESS_THAN -> {
                val firstParam = instruction.modeAwareParam(0, workingMemory)
                val secondParam = instruction.modeAwareParam(1, workingMemory)
                val saveIndex = instruction.parameters[2]
                workingMemory[saveIndex] = if (firstParam < secondParam) 1 else 0
            }
            OPCODE.EQUALS -> {
                val firstParam = instruction.modeAwareParam(0, workingMemory)
                val secondParam = instruction.modeAwareParam(1, workingMemory)
                val saveIndex = instruction.parameters[2]
                workingMemory[saveIndex] = if (firstParam == secondParam) 1 else 0
            }
            OPCODE.END -> {
                // chill
            }
        }
    }

    fun newPointer(pointer: Int, instruction: Instruction) : Int =
        when (instruction.opcode) {
            OPCODE.ADD -> pointer + 4
            OPCODE.MULTIPLY -> pointer + 4
            OPCODE.TAKE_INPUT -> pointer + 2
            OPCODE.SEND_OUTPUT -> pointer + 2
            OPCODE.JUMP_IF_TRUE -> if (instruction.modeAwareParam(0, workingMemory) == 0) pointer + 3 else pointer
            OPCODE.JUMP_IF_FALSE -> if (instruction.modeAwareParam(0, workingMemory) != 0) pointer + 3 else pointer
            OPCODE.LESS_THAN -> pointer + 4
            OPCODE.EQUALS -> pointer + 4
            OPCODE.END -> pointer
        }

    fun getReturnValue(): Int = lastOutput

    fun isTerminated(): Boolean = currentOpcode() == OPCODE.END

    fun currentOpcode(): OPCODE = extractInstruction(instructionPointer).opcode

}

data class Instruction(val opcode: OPCODE, val parameters: List<Int>, val parameterModes: List<Int>) {
    fun modeAwareParam(i: Int, workingMemory: MutableList<Int>) =
        when (parameterModes.getOrElse(i) { 0 }) {
            0 -> workingMemory[parameters[i]]
            1 -> parameters[i]
            else -> throw IllegalStateException("Invalid mode found")
        }
}