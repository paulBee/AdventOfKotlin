package year2019

import coOrdinates.Coordinate
import coOrdinates.DirectionRatio
import coOrdinates.QUADRANT
import productOf
import readLinesFromFile
import java.lang.IllegalStateException
import java.lang.Math.abs
import java.lang.RuntimeException
import java.util.Comparator

fun main () {
    val fileLines = readLinesFromFile("2019/day10.txt")

    val ySize = fileLines.size
    val xSize = fileLines[0].length

    val allDirections = buildDirections(xSize, ySize)
    val roidMap = buildRoidMap(fileLines)

    val bestLocation = roidMap.keys
        .maxByOrNull { countRoids(it, roidMap, allDirections) }
        ?:throw IllegalStateException("Guys! We got no roids!!")

    println("Best location is: $bestLocation")
    println("it can see ${countRoids(bestLocation, roidMap, allDirections)} roids")

    val first200 = allDirections.asSequence().sortedWith(DirectionSorter)
        .map { asteroidInDirection(bestLocation, roidMap, it) }
        .filter { it?.let { true } ?: false }
        .map { it?: throw RuntimeException("")}
        .take(200).toList()

    println("Value of roid 200 is: ${first200.last().let { (it.x * 100) + it.y }}")
    printGrid(first200, ySize, xSize)
}

private fun buildRoidMap(fileLines: List<String>): HashMap<Coordinate, TYPE> =
    fileLines
        .mapIndexed { y, line -> line.mapIndexed { x, char -> Pair(Coordinate(x, y), char.toString().roidOrVoid()) } }
        .flatten()
        .fold(HashMap()) { acc, pair -> acc[pair.first] = pair.second; acc }

private fun buildDirections(xSize: Int, ySize: Int): List<DirectionRatio> {
    val quarterOfDirections = productOf((1 until xSize - 1), (1 until ySize - 1))
        .map { DirectionRatio(it.first, it.second) }
        .map { it.toSimplestForm() }
        .toSet()

    val allDirections = quarterOfDirections.flatMap {
        listOf(
            it,
            DirectionRatio(-it.deltaX, it.deltaY),
            DirectionRatio(it.deltaX, -it.deltaY),
            DirectionRatio(-it.deltaX, -it.deltaY)
        )
    }.plus(
        listOf(
            DirectionRatio(0, 1),
            DirectionRatio(0, -1),
            DirectionRatio(1, 0),
            DirectionRatio(-1, 0)
        )
    )
    return allDirections
}

private fun printGrid(vapedRoids: List<Coordinate>, ySize: Int, xSize: Int) {
    println()
    println("Vaporization visualization")
    println((0..xSize*3).map { "=" }.joinToString(""))
    val foo = vapedRoids.mapIndexed() { i, it -> Pair(it, i + 1) }
        .fold(HashMap<Coordinate, Int>()) { acc, it -> acc[it.first] = it.second; acc }

    (0 until ySize).forEach { y ->
        println((0 until xSize).map { x -> foo[Coordinate(x, y)]?.toString() ?: " " }.map {
            it.padStart(
                3,
                ' '
            )
        }.joinToString(""))
    }
}

object DirectionSorter : Comparator<DirectionRatio> {
    override fun compare(p0: DirectionRatio?, p1: DirectionRatio?): Int {
        val first = p0?: throw IllegalStateException("which bugger put a null in here!!")
        val second = p1?: throw IllegalStateException("which bugger put a null in here!!")

        if (first == second) {
            return 0
        }

        val quadrant1 = first.quadrant()
        val quadrant2 = second.quadrant()

        return if (quadrant1 != quadrant2) {
            quadrant1.sortOrder().compareTo(quadrant2.sortOrder())
        } else {
            val mostYDir = calcGrad(first).compareTo(calcGrad(second))
            when (quadrant1) {
                QUADRANT.UPPER_RIGHT -> - mostYDir
                QUADRANT.LOWER_RIGHT -> mostYDir
                QUADRANT.LOWER_LEFT -> - mostYDir
                QUADRANT.UPPER_LEFT -> mostYDir
            }
        }
    }

    private fun calcGrad(direction: DirectionRatio): Float =
        if (direction.deltaX == 0)
            Float.MAX_VALUE
        else
            abs(direction.deltaY).toFloat() / abs(direction.deltaX).toFloat()

}

private fun QUADRANT.sortOrder() =
    when (this) {
        QUADRANT.UPPER_RIGHT -> 0
        QUADRANT.LOWER_RIGHT -> 1
        QUADRANT.LOWER_LEFT -> 2
        QUADRANT.UPPER_LEFT -> 3
    }

fun countRoids(point: Coordinate, roidMap: RoidMap, directions: List<DirectionRatio>) =
    directions
        .count { asteroidInDirection(point, roidMap, it)?.let { true } ?: false }


typealias RoidMap = Map<Coordinate, TYPE>



fun asteroidInDirection(point: Coordinate, roidMap: RoidMap, direction: DirectionRatio) =
    generateSequence(point) { it.follow(direction) }
        .drop(1) // dont include yourself
        .takeWhile { roidMap.containsKey(it) }
        .toList()
        .firstOrNull { roidMap[it] == TYPE.ROID }


enum class TYPE { ROID, VOID }

fun String.roidOrVoid () : TYPE =
    when (this) {
        "#" -> TYPE.ROID
        "." -> TYPE.VOID
        else -> throw IllegalStateException("ROID RAGE!!! I dont know what this is $this")
    }