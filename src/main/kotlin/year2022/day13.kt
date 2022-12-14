package year2022

import utils.aoc.readLinesFromFile
import utils.collections.chunkOnEmptyLine
import java.lang.RuntimeException

fun main() {
    val input = readLinesFromFile("2022/day13.txt").chunkOnEmptyLine()
    input.map { it[0].toPacket() to it[1].toPacket() }
        .mapIndexed { i, (left, right) -> if (left < right) i + 1 else 0 }
        .sum()
        .also { println(it) }

    val allPackets = input.flatMap { listOf(it[0].toPacket(), it[1].toPacket()) }

    val two = "[[2]]".toPacket()
    val six = "[[6]]".toPacket()

    ((allPackets.count { it < two } + 1) * (allPackets.count { it < six } + 2)).also { println(it) }

}

fun String.toPacket(): Packet {
    val contents = this.drop(1).dropLast(1)

    return if (contents.isEmpty()) {
        MultiData(emptyList())
    } else {
        val list = mutableListOf<Packet>()
        var charIndex = 0
        while (charIndex < contents.length) {
            if (contents[charIndex] == '[') {
                var bracketCount = 1
                var lookAhead = charIndex + 1
                while (bracketCount > 0) {
                    when (contents[lookAhead]) {
                        '[' -> bracketCount++
                        ']' -> bracketCount--
                    }
                    lookAhead++
                }
                list.add(contents.substring(charIndex, lookAhead).toPacket())
                charIndex = lookAhead + 1
            } else {
                val intString = contents.drop(charIndex).takeWhile { it.isDigit() }
                list.add(SingleData(intString.toInt()))

                val gumf = contents.drop(charIndex).drop(intString.length).takeWhile { !it.isDigit() && it != '[' }

                charIndex += intString.length + gumf.length
            }
        }
        MultiData(list)
    }
}

data class SingleData(val value: Int) : Packet
data class MultiData(val value: List<Packet>) : Packet

interface Packet : Comparable<Packet> {

    fun SingleData.toList() = MultiData(listOf(this))

    override operator fun compareTo(right: Packet): Int {
        val left = this

        return when {
            left is SingleData && right is SingleData -> left.value.compareTo(right.value)

            left is MultiData && right is MultiData ->
                left.value.zip(right.value)
                    .map { (a, b) -> a.compareTo(b) }
                    .firstOrNull { it != 0 } ?: left.value.size.compareTo(right.value.size)

            left is SingleData -> MultiData(listOf(left)).compareTo(right)
            right is SingleData -> left.compareTo(MultiData(listOf(right)))
            else -> throw RuntimeException("Unhandled case")
        }
    }
}




