package year2022

import utils.aoc.readLinesFromFile
import utils.collections.chunkOnEmptyLine

fun main() {
    val (startingInput, instructions) = readLinesFromFile("2022/day5.txt").chunkOnEmptyLine()

    val stacks9000 = startingInput.buildStacks()
    val moves = instructions.map { it.parseMove() }

    moves.forEach { move ->
        repeat(move.number) {
            stacks9000[move.to].addLast(stacks9000[move.from].removeLast())
        }
    }

    println(stacks9000.map { it.last() }.joinToString(""))



    val stacks9001 = startingInput.buildStacks()

    moves.forEach { move ->
        val craneJaw = ArrayDeque<Char>()
        repeat(move.number) {
            craneJaw.addLast(stacks9001[move.from].removeLast())
        }

        repeat(move.number) {
            stacks9001[move.to].addLast(craneJaw.removeLast())
        }
    }

    println(stacks9001.map { it.last() }.joinToString(""))

}

fun List<String>.buildStacks(): List<ArrayDeque<Char>> {
    val numberOfStacks = (this.maxOf { it.length } + 1) / 4
    val maxStackDepth = this.size - 1

    return (0 until numberOfStacks).map { stackIndex ->
        val stack = ArrayDeque<Char>()

        (1 .. maxStackDepth).forEach { depthIndex ->
            val depthView = this[maxStackDepth - (depthIndex)]
            val element = depthView[(stackIndex * 4) + 1]

            if (element != ' ') {
                stack.addLast(element)
            }

        }

        stack
    }
}

val regex = Regex("""^move (\d+) from (\d+) to (\d+)$""")
fun String.parseMove() =
    regex.matchEntire(this)?.destructured?.let { (a,b,c) -> Move(b.toInt() - 1 ,c.toInt() -1 ,a.toInt()) }!!


data class Move(val from: Int, val to: Int, val number: Int)