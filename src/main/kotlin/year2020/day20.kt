package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.multiply
import kotlin.math.sqrt

fun main() {
    val pieces = readLinesFromFile("2020/day20.txt").chunked(12).map { Piece(it) }

    pieces.forEach { it.identifyConnections(pieces) }

    val jigsaw = orientPieces(pieces.first { it.isCorner() }, blankRow(pieces))

    val image = jigsaw.toImage()

    val monsters = countSeaMonsters(image, 4)
    val choppyWater = image.sumBy { it.count { it == '#' } }

    displayPart2(choppyWater - monsters * 15)

    pieces.filter { it.isCorner() }.map { it.id }.multiply().also(displayPart1)
}

fun countSeaMonsters(image: List<String>, rotations: Int):Int {
    val rowLength = image.first().length
    val seaMonsters = (0 until image.size - 2).sumBy { y ->
        (0 until rowLength - 19).count { x -> isMonsterAt(x, y, image) }
    }
    return when {
        seaMonsters > 0 -> seaMonsters
        rotations > 0 -> countSeaMonsters(image.rotate(), rotations - 1)
        else -> countSeaMonsters(image.reversed(), 4)
//        what if there are no monsters? There is always monsters!
    }
}

typealias Jigsaw = List<List<Piece>>

fun Jigsaw.toImage() = this.flatMap { row ->
    row
        .map { it.contents }
        .reduce { acc, next -> acc.mapIndexed { index, string -> string + next[index] } }
}

fun isMonsterAt(x: Int, y: Int, image: List<String>) =
    image[y+1][x] == '#' &&
    image[y+2][x+1] == '#' &&
    image[y+2][x+4] == '#' &&
    image[y+1][x+5] == '#' &&
    image[y+1][x+6] == '#' &&
    image[y+2][x+7] == '#' &&
    image[y+2][x+10] == '#' &&
    image[y+1][x+11] == '#' &&
    image[y+1][x+12] == '#' &&
    image[y+2][x+13] == '#' &&
    image[y+2][x+16] == '#' &&
    image[y+1][x+17] == '#' &&
    image[y][x+18] == '#' &&
    image[y+1][x+18] == '#' &&
    image[y+1][x+19] == '#'

fun orientPieces(leftPiece: Piece?, rowAbove: List<Piece?>): List<List<Piece>> =
    if (leftPiece == null) {
        emptyList() // at bottom of jigsaw
    } else {
        val orientedRow = leftPiece.buildRow(null, rowAbove)
        listOf(orientedRow) + orientPieces(leftPiece.downSide.connectsTo, orientedRow)
    }

fun blankRow(pieces: List<Piece>): List<Piece?> {
    val rowSize =  sqrt(pieces.size.toDouble()).toInt()
    return (1..rowSize).map { null }
}

class Piece(val input: List<String>) {

    val id = input.first().split(" ")[1].split(":")[0].toLong()

    val square = input.drop(1).take(10)

    var upSide = Side(square.first())
    var downSide = Side(square.last())
    var leftSide = Side(square.map { it.first() }.joinToString(""))
    var rightSide = Side(square.map { it.last() }.joinToString(""))
    var contents = square.drop(1).dropLast(1).map { it.drop(1).dropLast(1) }
    val sides = listOf(upSide, downSide, leftSide, rightSide)

    fun isCorner() = sides.count { it.isEdge() } == 2

    fun identifyConnections(pieces: List<Piece>) {
        sides.forEach { it.findConnection(pieces.filter { it.id != id }) }
    }

    fun buildRow(onLeft: Piece?, topRow: List<Piece?>): List<Piece> {
        orientPiece(onLeft, topRow.first())

        val restOfRow: List<Piece> = rightSide.connectsTo?.buildRow(this, topRow.drop(1)) ?: emptyList()
        return listOf(this) + restOfRow
    }

    private fun orientPiece(onLeft: Piece?, onTop: Piece?) {
        while (leftSide.connectsTo != onLeft) {
            rotatePiece()
        }

        if (upSide.connectsTo != onTop) {
            flipVertical()
        }
    }

    private fun flipVertical() {
        val tmp = upSide
        upSide = downSide
        downSide = tmp

        contents = contents.reversed()
    }

    private fun rotatePiece() {
        val tmp = upSide
        upSide = rightSide
        rightSide = downSide
        downSide = leftSide
        leftSide = tmp

        // I know there are better ways to track this, but this is more fun :)
        contents = contents.rotate()
    }

}

fun List<String>.rotate() =
    this.indices.map { y: Int -> this.indices.map { x: Int -> this[x][y] }.joinToString("") }.reversed()

class Side(val shape: String) {
    var connectsTo: Piece? = null

    fun isMatch(otherShape: String) = shape == otherShape || shape.reversed() == otherShape

    fun isEdge() = connectsTo == null

    fun findConnection(otherPieces: List<Piece>) {
        connectsTo = otherPieces.firstOrNull { op -> op.sides.any { it.isMatch(shape) } }
    }

}
