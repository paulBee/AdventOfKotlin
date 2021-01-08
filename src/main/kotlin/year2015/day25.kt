package year2015

import utils.aoc.displayPart1

fun main() {
    val row = 2981
    val column = 3075

    generateSequence(20151125L) { (it * 252533L) % 33554393L }
        .elementAt(rcToInt(row,column) - 1).also { displayPart1(it) }

}

fun rcToInt(row: Int, column: Int): Int {
    val diagonalMax = triangleNumbers.elementAt(row + column - 2)

    return (diagonalMax - (row - 1))
}

val triangleNumbers = sequence {
    var element = 0
    var count = 0
    while (true) {
        count += ++element
        yield(count)
    }
}