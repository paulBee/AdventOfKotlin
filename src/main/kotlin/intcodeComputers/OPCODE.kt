package intcodeComputers

enum class OPCODE { ADD, MULTIPLY, END }

fun Int.toOpcode(): OPCODE =
    when (this) {
        1 -> OPCODE.ADD
        2 -> OPCODE.MULTIPLY
        99 -> OPCODE.END
        else -> throw RuntimeException("good god its all gone Pete Tong!")
    }