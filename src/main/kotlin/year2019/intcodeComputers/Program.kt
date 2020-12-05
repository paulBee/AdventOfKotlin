package year2019.intcodeComputers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel

@ExperimentalCoroutinesApi
class Program(
    instructions: List<Long>,
    private val input: Channel<Long> = Channel(Int.MAX_VALUE),
    private val output: Channel<Long> = Channel(Int.MAX_VALUE),
    private val name: String = "program"
) {
    private val workingMemory = instructions.foldIndexed(HashMap<Long, Long>()) { index, acc, it ->
        acc[index.toLong()] = it
        acc
     }
    private var instructionPointer = 0L
    private var relativeBase = 0L
    private var lastOutput = 0L
    private var savedInstruction: Instruction? = null

    var inputsRequested = 0


    suspend fun run(): Long {
        while (true) {
            val instruction = runInstruction()
            if (instruction.opcode == OPCODE.END) {
                println("$name is finished")
                return getReturnValue()
            }
        }
    }

    suspend fun runUntilInput(): InputOrDone {
        savedInstruction?.run {
            runEffects(this)
            savedInstruction = null
        }
       return loopUntilInput()
    }

    private tailrec suspend fun loopUntilInput(): InputOrDone {
        val nextInstruction = extractInstruction()
//        println(nextInstruction)
        return when(nextInstruction.opcode) {
            OPCODE.END -> {
                println("program finished")
                InputOrDone(true, getReturnValue())
            }
            OPCODE.TAKE_INPUT -> {
                savedInstruction = nextInstruction
                InputOrDone(false)
            }
            else -> {
                runEffects(nextInstruction)
                loopUntilInput()
            }
        }
    }

    data class InputOrDone(val completed: Boolean, val returnCode: Long = 0) {

    }

    private suspend fun runInstruction(): Instruction {
        val nextInstruction = extractInstruction()
//        println(nextInstruction)
        runEffects(nextInstruction)
        return nextInstruction
    }

    private fun extractInstruction(): Instruction {
        val opcodeInfo = workingMemory[instructionPointer].toString()
        val opcode = opcodeInfo.takeLast(2).toInt().toOpcode()
        val parameterModes = opcodeInfo
            .dropLast(2)
            .reversed()
            .map { it.toString().toInt() }

        val immediateParameters = (instructionPointer..instructionPointer + opcode.parameterSize()).drop(1)
            .map { workingMemory.getOrDefault(it, 0) }


        return Instruction(opcode, immediateParameters, parameterModes, relativeBase)
    }

    private suspend fun runEffects(instruction: Instruction) {
        when (instruction.opcode) {
            OPCODE.ADD -> {
                val number1 = instruction.getterMode(0, workingMemory)
                val number2 = instruction.getterMode(1, workingMemory)
                val saveIndex = instruction.setterMode(2)
                workingMemory[saveIndex] = number1 + number2
                instructionPointer += 4
            }
            OPCODE.MULTIPLY -> {
                val number1 = instruction.getterMode(0, workingMemory)
                val number2 = instruction.getterMode(1, workingMemory)
                val saveIndex = instruction.setterMode(2)
                workingMemory[saveIndex] = number1 * number2
                instructionPointer += 4
            }
            OPCODE.TAKE_INPUT -> {
                val saveIndex = instruction.setterMode(0)
                takeFromInput(saveIndex)
                instructionPointer += 2
            }
            OPCODE.SEND_OUTPUT -> {
                val outputVal = instruction.getterMode(0, workingMemory)
                lastOutput = outputVal
                output.send(outputVal)
                instructionPointer += 2
            }
            OPCODE.JUMP_IF_TRUE -> {
                if (instruction.getterMode(0, workingMemory) != 0L) {
                    instructionPointer = instruction.getterMode(1, workingMemory)
                } else {
                    instructionPointer += 3
                }
            }
            OPCODE.JUMP_IF_FALSE -> {
                if (instruction.getterMode(0, workingMemory) == 0L) {
                    instructionPointer = instruction.getterMode(1, workingMemory)
                } else {
                    instructionPointer += 3
                }
            }
            OPCODE.LESS_THAN -> {
                val firstParam = instruction.getterMode(0, workingMemory)
                val secondParam = instruction.getterMode(1, workingMemory)
                val saveIndex = instruction.setterMode(2)
                workingMemory[saveIndex] = if (firstParam < secondParam) 1L else 0L
                instructionPointer += 4
            }
            OPCODE.EQUALS -> {
                val firstParam = instruction.getterMode(0, workingMemory)
                val secondParam = instruction.getterMode(1, workingMemory)
                val saveIndex = instruction.setterMode(2)
                workingMemory[saveIndex] = if (firstParam == secondParam) 1L else 0L
                instructionPointer += 4
            }
            OPCODE.BASE_OFFSET -> {
                val firstParam = instruction.getterMode(0, workingMemory)
                relativeBase += firstParam
                instructionPointer += 2
            }
            OPCODE.END -> {
                // chill
            }
        }
    }

    private suspend fun takeFromInput(saveIndex: Long) {
//        println("asking for input")
        inputsRequested++
        workingMemory[saveIndex] = input.receive()
    }

    private fun getReturnValue(): Long = workingMemory.getOrDefault(0L, 0)
}

data class Instruction(val opcode: OPCODE, val parameters: List<Long>, val parameterModes: List<Int>, val baseOffset: Long) {
    fun getterMode(i: Int, workingMemory: HashMap<Long, Long>) =
        when (parameterModes.getOrElse(i) { 0 }) {
            0 -> workingMemory.getOrDefault(parameters[i], 0)
            1 -> parameters[i]
            2 -> workingMemory.getOrDefault(baseOffset + parameters[i], 0)
            else -> throw IllegalStateException("Invalid mode found")
        }

    fun setterMode(i: Int) =
        when (parameterModes.getOrElse(i) { 0 }) {
            0 -> parameters[i]
            1 -> parameters[i]
            2 -> baseOffset + parameters[i]
            else -> throw IllegalStateException("Invalid mode found")
        }
}

@ExperimentalCoroutinesApi
class InputOutputComputer(programInstructions: List<Long>) {

    private val input = Channel<Long>(Int.MAX_VALUE)
    private val output = Channel<Long>(Int.MAX_VALUE)
    private val program = Program(programInstructions, input, output)

    suspend fun start() {
        program.runUntilInput()
    }
    suspend fun getOutputForInput(next: Long) : List<Long> {
        input.send(next)
        program.runUntilInput()
        return drainOutput()
    }

    private suspend fun drainOutput() = drainOutput(output)
}

@ExperimentalCoroutinesApi
suspend fun drainOutput(channel: Channel<Long>): List<Long> {
    val outputs = ArrayList<Long>()
    while (!channel.isEmpty) {
        outputs.add(channel.receive())
    }
    return outputs
}