package utils.strings

import kotlin.math.pow

/**
 * Splits the string in to 2 strings
 * size = if positive sets the size of the starting string, if negative the size of the ending string
 */
fun String.splitAtIndex(size: Int) =
    if (size < 0) {
        Pair(this.take(this.length + size), this.drop(this.length + size))
    } else {
        Pair(this.take(size), this.drop(size))
    }


/**
 * parse a String to Int using arbitrary characters for any base
 * all chars must be provided with the first being interpreted as 0, the second 1, and so on
 */
fun toIntUsingDigitsOf(vararg numberChars: Char): (String) -> Int {

    val validationRegex = numberChars.joinToString("", "^[", "]$").toRegex()
    val digitLookup = numberChars.mapIndexed { index, c -> c to index }.toMap()
    val base = numberChars.size.toDouble()

    return { string ->
        if (validationRegex.matches(string)) throw RuntimeException("That is BAD intelligence! $string has chars other than ${numberChars.joinToString(", ")}")
        string.reversed()
            .map { digitLookup[it]!! }
            .foldIndexed(0) { index, acc, next -> acc + next * base.pow(index).toInt() }
    }
}