
fun hasPairedDigits (digitString : String) : Boolean =
    digitString.fold(FoldData('?', 1, false)) { acc, char ->
        if (acc.lastChar == char) {
            FoldData(char, acc.seenInARow + 1, acc.pairSpotted)
        } else {
            FoldData(char, 1, acc.pairSpotted || acc.seenInARow == 2)
        }
    }.let {
        it.pairSpotted || it.seenInARow == 2
    }

fun doesntDecrease (digitString: String) : Boolean =
    digitString.windowed(2).none { it[0].toInt() > it[1].toInt() }

fun main() {
    // sort of cheating not parsing the input, but this code is not improved by the extra regex
    val validPasswords = (158126..624574)
        .filter { hasPairedDigits(it.toString()) && doesntDecrease(it.toString()) }
        .count()

    println(validPasswords)
}

data class FoldData(val lastChar: Char, val seenInARow: Int, val pairSpotted: Boolean)