package year2019

import utils.navigation.Coordinate
import utils.aoc.readLinesFromFile

fun main() {
    tryAllPaths()
}

fun tryAllPaths() {
    val grid = readLinesFromFile("2019/day18.txt").mapIndexed { y, line ->
        line.mapIndexed { x, char -> Pair(Coordinate(x,y), char) }
    }.flatten()


    val nodes = grid.filter { it.second.isLetter() || it.second == '@' }
        .map { Pair(it.second, adjacentLetters(it.first, grid.asFuncLeftToRight())) }

    println(walkUntilComplete(nodes.first { it.first == '@' }, nodes, listOf(LetterAndDistance('@', 0))))
}

fun walkUntilComplete(currentLetter: LetterNode, letterNodes: List<LetterNode>, pathSoFar: List<LetterAndDistance>, finishedLengths: HashSet<Int> = HashSet()): List<List<LetterAndDistance>> {
    val nextLettersDuplicates = possibleNextSteps(currentLetter, letterNodes, pathSoFar)
    val nextLetters = nextLettersDuplicates.filter { nextLettersDuplicates.none { d -> d.letter == it.letter && d.distance < it.distance } }.distinct()

    if (pathSoFar.size < 17) { println(pathSoFar) }
    return when {
        nextLetters.isEmpty() -> {
            finishedLengths.add(pathSoFar.sumBy { it.distance })
            listOf(pathSoFar)
        }
        finishedLengths.any { length -> length < pathSoFar.sumBy { p -> p.distance } } -> emptyList()
        else -> nextLetters
            .sortedBy { it.distance }
            .flatMap {
                walkUntilComplete(letterNodes.first { node -> node.first == it.letter}, letterNodes, pathSoFar.plus(it), finishedLengths)
            }
    }
}

fun possibleNextSteps(currentLetter: LetterNode, letterNodes: List<LetterNode>, pathSoFar: List<LetterAndDistance>, exploredNodes: Set<Char> = HashSet(), distance: Int = 0): List<LetterAndDistance> {
    return currentLetter.second
        .filter { !exploredNodes.contains(it.letter) }
        .flatMap {
            when {
                it.letter.isNextLocationCandidate(pathSoFar) -> listOf(LetterAndDistance(it.letter, it.distance + distance))
                !it.letter.isPassable(pathSoFar) -> emptyList()
                else -> {
                    possibleNextSteps(letterNodes.first { nodes -> nodes.first == it.letter}, letterNodes, pathSoFar, exploredNodes.plus(it.letter), it.distance + distance)
                }
            }
        }
}

typealias LetterNode = Pair<Char, List<LetterAndDistance>>

fun adjacentLetters(
    coordinate: Coordinate,
    charAtCoordinate: (Coordinate) -> Char,
    distance: Int = 1,
    searched: MutableSet<Coordinate> = HashSet()
): List<LetterAndDistance> {
    searched.add(coordinate)
    return coordinate.allAdjacent()
        .filter { !searched.contains(it) }
        .map { Pair(it, charAtCoordinate.invoke(it)) }
        .filter { it.second.isWalkable() }
        .flatMap {
            when {
                it.second.isLetter() || it.second == '@'  -> listOf(LetterAndDistance(it.second, distance))
                else -> adjacentLetters(it.first, charAtCoordinate, distance + 1, searched)
            }
        }
}

private fun Char.isNextLocationCandidate(collected: List<LetterAndDistance>) =
    collected.none { it.letter == this } &&
            (this.isLowerCase() || (this.isUpperCase() && collected.any{ it.letter == this.toLowerCase() }))


private fun Char.isPassable(collected: List<LetterAndDistance>) =
    !this.isUpperCase() || collected.any{ it.letter == this.toLowerCase() }

private fun Char.isWalkable() = this != '#'

data class LetterAndDistance(val letter: Char, val distance: Int)

private fun <S, T> List<Pair<S, T>>.asFuncLeftToRight(): (S) -> T = { left -> this.first { it.first == left }.second }
