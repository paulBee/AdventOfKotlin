package utils.strings

import utils.hof.oneOf
import java.math.BigInteger
import java.security.MessageDigest
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

val regex = Regex("(\\w)(\\d+)")
fun String.toLetterAndNumber(): Pair<String, Int> {
    val (letter, distance) = regex.matchEntire(this)?.destructured?: throw RuntimeException(this)
    return letter to distance.toInt()
}

fun String.isNumber() =
    if (this[0] == '-') {
        this.drop(1).all { it.isDigit() }
    } else {
        this.all { it.isDigit() }
    }

fun String.md5(): String {
    val digest = MessageDigest.getInstance("MD5").digest(this.toByteArray(Charsets.UTF_8))
    val hexMd5 = BigInteger(1, digest).toString(16)
    return hexMd5.padStart(32, '0')
}

fun Char.isVowel() = oneOf('a', 'e', 'i', 'o', 'u')(this.toLowerCase())

fun Char.shift() = when (this) {
    'z' -> 'a'
    else -> this + 1
}

fun Char.shift(number: Int) = generateSequence(this) { it.shift() }.elementAt(number)