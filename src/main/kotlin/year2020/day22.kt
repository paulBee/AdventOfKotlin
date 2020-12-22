package year2020

import kotlinx.collections.immutable.*
import utils.aoc.displayPart1
import utils.aoc.readLinesFromFile
import utils.collections.dropP
import utils.collections.headAndTail
import utils.collections.takeP
import utils.strings.isNumber
import utils.types.Either
import utils.types.Left
import utils.types.Right

fun main() {
    val input = readLinesFromFile("2020/day22.txt")

    val deck1 = input.drop(1).takeWhile { it.isNotBlank() }.map { it.toInt() }.toPersistentList()
    val deck2 = input.takeLastWhile { it.isNumber() }.map { it.toInt() }.toPersistentList()

    playCombat(deck1, deck2).also { displayPart1(it.calculateScore()) }

    playRecursiveCombat(deck1, deck2)
        .also { winner -> winner.left?.also { println("I win! with score: ${it.calculateScore()}") } }
        .also { winner -> winner.right?.also { println("Crab wins! with score: ${it.calculateScore()}") } }
}

private fun List<Int>.calculateScore(): Int =
    this.reversed().foldIndexed(0) { index, acc, next -> acc + ((index + 1) * next) }

private fun playCombat(deck1: PersistentList<Int>, deck2: PersistentList<Int>): PersistentList<Int> =
    when {
        deck1.isEmpty() -> deck2
        deck2.isEmpty() -> deck1
        else -> when {
            deck1.first() > deck2.first() -> playCombat(
                deck1.dropP(1) + listOf(deck1.first(), deck2.first()),
                deck2.dropP(1)
            )
            else -> playCombat(
                deck1.dropP(1),
                deck2.dropP(1) + listOf(deck2.first(), deck1.first())
            )
        }
    }

tailrec fun playRecursiveCombat(deck1: PersistentList<Int>, deck2: PersistentList<Int>, history: PersistentSet<Pair<List<Int>, List<Int>>> = persistentSetOf()): Either<List<Int>, List<Int>> {
    return when {
        deck1.isEmpty() -> Right(deck2)
        deck2.isEmpty() -> Left(deck1)
        history.contains(deck1 to deck2) -> Left(deck1)
        else -> when (determineRound(deck1, deck2)) {
            is Player1 -> playRecursiveCombat(
                deck1.dropP(1) + listOf(deck1.first(), deck2.first()),
                deck2.dropP(1),
                history + (deck1 to deck2))
            is Player2 -> playRecursiveCombat(
                deck1.dropP(1),
                deck2.dropP(1) + listOf(deck2.first(), deck1.first()),
                history + (deck1 to deck2)
            )
        }
    }
}

fun determineRound(deck1: PersistentList<Int>, deck2: PersistentList<Int>): RoundWinner {
    val (card1, rest1) = deck1.headAndTail()
    val (card2, rest2) = deck2.headAndTail()
    return when {
        card1 <= rest1.size && card2 <= rest2.size -> playRecursiveCombat(rest1.takeP(card1), rest2.takeP(card2))
            .let { if (it is Left) Player1 else Player2}
        card1 > card2 -> Player1
        else -> Player2
    }
}

sealed class RoundWinner
object Player1: RoundWinner()
object Player2: RoundWinner()
