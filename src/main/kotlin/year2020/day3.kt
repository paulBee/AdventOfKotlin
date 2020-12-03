package year2020

import displayPart1
import displayPart2
import readLinesFromFile

fun main() {
    val treeSurvey = TreeSurvey(readLinesFromFile("2020/day3.txt"))

    listOf(
        treeSurvey.walkPath(right1Down1).count { it.isTree() },
        treeSurvey.walkPath(right3Down1).count { it.isTree() }.also(displayPart1),
        treeSurvey.walkPath(right5Down1).count { it.isTree() },
        treeSurvey.walkPath(right7Down1).count { it.isTree() },
        treeSurvey.walkPath(right1Down2).count { it.isTree() }
    )
        .map{ it.toLong() }
        .reduce { acc, i -> acc * i  }
        .also(displayPart2)
}

class TreeSurvey(val data: List<String>) {

    fun walkPath(gradient: Gradient) =
        generateSequence(0) { it + 1 }
            .map { gradient(it) }
            .takeWhile { (_, y) -> y < data.size }
            .map { (x, y) -> data[y].getCircular(x) }
            .toList()
}

typealias Gradient = (Int) -> Pair<Int, Int>

val right1Down1: Gradient = { step: Int -> Pair(step, step) }
val right3Down1: Gradient = { step: Int -> Pair(step * 3, step) }
val right5Down1: Gradient = { step: Int -> Pair(step * 5, step) }
val right7Down1: Gradient = { step: Int -> Pair(step * 7, step) }
val right1Down2: Gradient = { step: Int -> Pair(step, step * 2) }

fun String.getCircular(index: Int) = this[index % this.length]

fun Char.isTree() = this == '#'
