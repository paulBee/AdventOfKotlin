package intcodeComputers

enum class OPCODE {
    ADD, MULTIPLY,
    TAKE_INPUT, SEND_OUTPUT,
    JUMP_IF_TRUE, JUMP_IF_FALSE,
    LESS_THAN, EQUALS,
    END;

    fun parameterSize(): Int =
        when(this) {
            ADD -> 3
            MULTIPLY -> 3
            TAKE_INPUT -> 1
            SEND_OUTPUT -> 1
            JUMP_IF_TRUE -> 2
            JUMP_IF_FALSE -> 2
            LESS_THAN -> 3
            EQUALS -> 3
            END -> 0
        }
}

fun Int.toOpcode(): OPCODE =
    when (this) {
        1 -> OPCODE.ADD
        2 -> OPCODE.MULTIPLY
        3 -> OPCODE.TAKE_INPUT
        4 -> OPCODE.SEND_OUTPUT
        5 -> OPCODE.JUMP_IF_TRUE
        6 -> OPCODE.JUMP_IF_FALSE
        7 -> OPCODE.LESS_THAN
        8 -> OPCODE.EQUALS
        99 -> OPCODE.END
        else -> throw RuntimeException("good god its all gone Pete Tong! $this is no opcode")
    }