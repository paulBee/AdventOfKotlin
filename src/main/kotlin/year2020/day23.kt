package year2020

import kotlinx.collections.immutable.*
import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.collections.dropP
import utils.collections.takeP

fun main() {

    generateSequence(persistentListOf(3,2,6,5,1,9,4,7,8)) { it.popAndSnip().insertAt(9) }
        .drop(100)
        .first()
        .let { it.takeLastWhile { it != 1 } + it.takeWhile { it != 1 } }
        .let { it.joinToString("") }
        .also(displayPart1)

    runCups(listOf(3,2,6,5,1,9,4,7,8) + (10..1_000_000))
        .take(10_000_000)
        .last().also { displayPart2(it.first.toLong() * it.second.toLong()) }

}

fun runCups(initial: List<Int>): Sequence<Pair<Int, Int>> {
    val maxNumber = initial.max()
    val numberNextTo: MutableMap<Int,Int> = (initial + initial.take(1)).zipWithNext().toMap().toMutableMap()
    var current = 3
    return sequence {
        while (true) {
            val snippet = generateSequence(current) { numberNextTo[it]!! }.drop(1).take(3).toList()

            val nextCurrent = numberNextTo[snippet.last()]!!


            val insertPoint = generateSequence(current) { if (it == 1) maxNumber else it - 1}.drop(1).first { it !in snippet }

            numberNextTo[current] = nextCurrent
            numberNextTo[snippet.last()] = numberNextTo[insertPoint]!!
            numberNextTo[insertPoint] = snippet.first()
            current = nextCurrent

            val first = numberNextTo[1]!!
            val second = numberNextTo[first]!!

            yield(first to second)
        }
    }
}

fun PersistentList<Int>.popAndSnip() = Triple(this.first(), this.takeP(4).dropP(1), this.dropP(4) + this.first())

fun Triple<Int, PersistentList<Int>, PersistentList<Int>>.insertAt(max: Int): PersistentList<Int> {
    val (source, toInsert, insertIn) = this

    val target = generateSequence(source) { if (it == 1) max else it - 1 }
        .drop(1)
        .first { insertIn.contains(it) }

    return insertIn.takeWhileP { it != target } + listOf(target) + toInsert + insertIn.takeLastWhileP { it != target }
}

private fun <E> PersistentList<E>.takeWhileP(f: (E) -> Boolean) =
    this.takeWhile(f).toPersistentList()

private fun <E> PersistentList<E>.takeLastWhileP(f: (E) -> Boolean) =
    this.takeLastWhile(f).toPersistentList()
