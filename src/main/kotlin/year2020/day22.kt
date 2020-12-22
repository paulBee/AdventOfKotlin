package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.headAndTail
import utils.strings.isNumber
import utils.types.Either
import utils.types.Left
import utils.types.Right

fun main() {
    val input = readLinesFromFile("2020/day22.txt")
    val deck1 = input.drop(1).takeWhile { it.isNotBlank() }.map { it.toInt() }
    val deck2 = input.takeLastWhile { it.isNumber() }.map { it.toInt() }

    val previous = setOf(deck1 to deck2)

    println(previous.contains(deck2 to deck1))

    playCombat(deck1, deck2).also { displayPart1(it.calculateScore()) }

    playRecursiveCombat(deck1, deck2)
        .also { it.left?.also { println("I win! with score: ${it.calculateScore()}") } }
        .also { it.right?.also { println("Crab wins! with score: ${it.calculateScore()}") } }

    playRecursiveCombat(listOf(9, 2, 6, 3, 1), listOf(5, 8, 4, 7, 10))
        .also { it.left?.also { println("I win! with score: ${it.calculateScore()}") } }
        .also { it.right?.also { println("Crab wins! with score: ${it.calculateScore()}") } }
}

private fun List<Int>.calculateScore(): Int =
    this.reversed().foldIndexed(0) { index, acc, next -> acc + ((index + 1) * next) }

private fun playCombat(deck1: List<Int>, deck2: List<Int>): List<Int> =
    when {
        deck1.isEmpty() -> deck2
        deck2.isEmpty() -> deck1
        else -> when {
            deck1.first() > deck2.first() -> playCombat(
                deck1.drop(1) + listOf(deck1.first(), deck2.first()),
                deck2.drop(1)
            )
            else -> playCombat(deck1.drop(1), deck2.drop(1) + listOf(deck2.first(), deck1.first()))
        }
    }

tailrec fun playRecursiveCombat(deck1: List<Int>, deck2: List<Int>, history: Set<Pair<List<Int>, List<Int>>> = emptySet()): Either<List<Int>, List<Int>> {
    return when {
        deck1.isEmpty() -> Right(deck2)
        deck2.isEmpty() -> Left(deck1)
        history.contains(deck1 to deck2) -> Left(deck1)
        else -> {
            val (card1, rest1) = deck1.headAndTail()
            val (card2, rest2) = deck2.headAndTail()
            when {
                card1 <= rest1.size && card2 <= rest2.size ->
                    playRecursiveCombat(rest1.take(card1), rest2.take(card2)).let { result ->
                        when (result) {
                            is Left -> playRecursiveCombat(rest1 + listOf(card1, card2), rest2, history + (deck1 to deck2))
                            is Right -> playRecursiveCombat(rest1, rest2 + listOf(card2, card1), history + (deck1 to deck2))
                        }
                    }
                card1 > card2 -> playRecursiveCombat(rest1 + listOf(card1, card2), rest2, history + (deck1 to deck2))
                else -> playRecursiveCombat(rest1, rest2 + listOf(card2, card1), history + (deck1 to deck2))
            }
        }
    }
}
