package year2020

import kotlinx.collections.immutable.*
import utils.aoc.displayPart1
import utils.aoc.readLinesFromFile
import utils.collections.dropP
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

private fun playCombat(deck1: Deck, deck2: Deck): Deck =
    when {
        deck1.isEmpty() -> deck2
        deck2.isEmpty() -> deck1
        deck1.first() > deck2.first() -> playCombat(deck1.winCard(deck2.first()), deck2.lost())
        else -> playCombat(deck1.lost(), deck2.winCard(deck1.first()))
    }

tailrec fun playRecursiveCombat(deck1: Deck, deck2: Deck, history: PersistentSet<Pair<Deck, Deck>> = persistentSetOf()): Either<Deck, Deck> =
    when {
        deck1.isEmpty() -> Right(deck2)
        deck2.isEmpty() -> Left(deck1)
        history.contains(deck1 to deck2) -> Left(deck1)
        else -> when (roundWinner(deck1, deck2)) {
            is Player1 -> playRecursiveCombat(deck1.winCard(deck2.first()), deck2.lost(), history + (deck1 to deck2))
            is Player2 -> playRecursiveCombat(deck1.lost(), deck2.winCard(deck1.first()), history + (deck1 to deck2))
        }
    }

fun roundWinner(deck1: Deck, deck2: Deck): RoundWinner =
    when {
        deck1.canRecurse() && deck2.canRecurse() -> playRecursiveCombat(deck1.recurse(), deck2.recurse()).toPlayer()
        deck1.first() > deck2.first() -> Player1
        else -> Player2
    }

typealias Deck = PersistentList<Int>
fun Deck.winCard(card: Int) = this.dropP(1) + listOf(this.first(), card)
fun Deck.lost() = this.dropP(1)
fun Deck.canRecurse() = this.first() < this.size
fun Deck.recurse() = this.first().let { this.dropP(1).takeP(it)}
fun Deck.calculateScore(): Int = this.reversed().foldIndexed(0) { index, acc, next -> acc + ((index + 1) * next) }

fun Either<Deck, Deck>.toPlayer() = this.let { if (it is Left) Player1 else Player2}

sealed class RoundWinner
object Player1: RoundWinner()
object Player2: RoundWinner()
