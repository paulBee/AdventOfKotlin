package intcodeComputers

class Program(private val workingMemory: MutableList<Int>) {

    private val instructionSize = 4
    private var instructionPointer = 0

    fun isTerminated(): Boolean = currentOpcode() == OPCODE.END

    fun executeInstruction(opcodeImplementation: (OPCODE) -> (Int, Int) -> Int) {
        val param1 = workingMemory[opCodeIndex() + 1]
        val param2 = workingMemory[opCodeIndex() + 2]
        val param3 = workingMemory[opCodeIndex() + 3]

        val input1 = workingMemory[param1]
        val input2 = workingMemory[param2]

        val instructionResult = opcodeImplementation(currentOpcode())(input1, input2)
        workingMemory[param3] = instructionResult
        instructionPointer++
    }

    fun getReturnValue(): Int = workingMemory[0]

    private fun currentOpcode() = workingMemory[opCodeIndex()].toOpcode()

    private fun opCodeIndex() : Int = instructionPointer * instructionSize
}