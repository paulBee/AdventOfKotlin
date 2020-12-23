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

    val input = persistentListOf(3,2,6,5,1,9,4,7,8) + (10..1_000_000)

    generateSequence(input) { it.popAndSnip().insertAt(1_000_000) }
        .mapIndexed { i, it -> it.also { println(i) }}
        .drop(1_000_000)
        .first()
        .let { it.takeLastWhile { it != 1 } + it.takeWhile { it != 1 } }
        .also { displayPart2(it.first() * it.drop(1).first()) }
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
