
fun hasPairedDigits (password : String) : Boolean =
    password.toList().chunkOnChange().any { it.size == 2 }

fun doesntDecrease (password: String) : Boolean =
    password.windowed(2).none { it[0].toInt() > it[1].toInt() }

fun main() {
    // sort of cheating not parsing the input, but this code is not improved by the extra regex
    val validPasswords = (158126..624574)
        .filter { hasPairedDigits(it.toString()) && doesntDecrease(it.toString()) }
        .count()

    println(validPasswords)
}