package year2019

import chunkWhen

fun hasRepeat (password: String) : Boolean =
    password.windowed(2).any { it[0] == it[1] }

fun hasPairedDigits (password : String) : Boolean =
    password.toList()
        .chunkWhen { a, b -> a != b }
        .any { it.size == 2 }

fun doesntDecrease (password: String) : Boolean =
    password.windowed(2).none { it[0].toInt() > it[1].toInt() }

fun main() {
    // sort of cheating not parsing the input, but this code is not improved by the extra regex
    val validPasswordsPart1 = (158126..624574).map { it.toString() }
        .filter { hasRepeat(it) && doesntDecrease(it) }
    val validPasswordsPart2 = (158126..624574).map { it.toString() }
        .filter { hasPairedDigits(it) && doesntDecrease(it) }

    println("Part 1 has ${validPasswordsPart1.count()} passwords")
    println("Part 2 has ${validPasswordsPart2.count()} passwords")
}