package intcodeComputers

class Computer (private val program : List<Int>) {

    fun runProgram(noun: Int, verb: Int) : Int {
        val program = configureProgram(noun, verb)

        while (!program.isTerminated()) {
            program.executeInstruction(::opcodeImplementation)
        }

        return program.getReturnValue()
    }

    private fun configureProgram(noun: Int, verb: Int): Program {
        val workingMemory = program.toMutableList()
        workingMemory[1] = noun
        workingMemory[2] = verb
        return Program(workingMemory)
    }

    private fun opcodeImplementation(opcode: OPCODE):  (Int, Int) -> Int =
        when (opcode) {
            OPCODE.ADD -> ::addition
            OPCODE.MULTIPLY -> ::multiplication
            OPCODE.END -> throw RuntimeException("program should have terminated")
        }

    private fun addition(a : Int, b : Int): Int = a + b
    private fun multiplication(a: Int, b: Int): Int = a * b
}