package utils.navigation

data class MoveInstruction (val direction : DIRECTION, val distance : Int)

val moveRegex = """^([UDRL])(\d+)$""".toRegex()

fun String.toMoveInstruction() : MoveInstruction {
    val (direction, distance) =
        moveRegex.matchEntire(this)
            ?.destructured
            ?: throw IllegalArgumentException("String $this does not match regex")

    return MoveInstruction(direction = direction.toDirection(), distance = distance.toInt())
}