package year2022

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.takeUntilStable
import utils.collections.takeWhileInclusive
import utils.navigation.*

fun main() {

    val initialCave = readLinesFromFile("2022/day14.txt").toCave()

    val animateSand = generateSequence(initialCave) { cave -> cave + (sandPathThroughCave(cave).last() to Material.Sand) }

    animateSand.takeWhile { it.hasNoSandLowerThanRock() }
        .last()
        .also { finalCave ->

            print(finalCave.gridToString { it?.toString() ?: " "})

            displayPart1(finalCave.count { it.value == Material.Sand })

        }

    animateSand.takeWhileInclusive { it[Coordinate(500, 0)] != Material.Sand }
        .last()
        .also { finalCave ->

            print(finalCave.gridToString{ it?.toString() ?: " "})

            displayPart2(finalCave.count { it.value == Material.Sand })

        }
}

private fun sandPathThroughCave(cave: Cave): Sequence<Coordinate> {

    val maxCaveDepth = cave.filter { it.value == Material.Rock }.maxOf { it.key.y }

    return generateSequence(Coordinate(500, 0)) { lastPosition ->

        listOf(
            lastPosition.move(Down),
            lastPosition.move(Down).move(Left),
            lastPosition.move(Down).move(Right)
        ).firstOrNull {
            cave.materialAt(it, maxCaveDepth) == Material.Air
        } ?: lastPosition

    }.takeUntilStable()
}

typealias Cave = Map<Coordinate, Material>

private fun Cave.hasNoSandLowerThanRock(): Boolean {
    val coordsByValue = this.entries.groupBy { it.value }
    val maxSand = coordsByValue[Material.Sand]?.maxOf { it.key.y } ?: Int.MIN_VALUE
    val maxRock = coordsByValue[Material.Rock]?.maxOf { it.key.y } ?: Int.MIN_VALUE
    return maxRock > maxSand
}

fun Cave.materialAt(coordinate: Coordinate, maxCaveDepth: Int) =
    if (coordinate.y == maxCaveDepth + 2) Material.Rock
    else this[coordinate] ?: Material.Air

enum class Material {
    Sand, Rock, Air;

    override fun toString() = when(this) {
        Air -> " "
        Rock -> "#"
        Sand -> "O"
    }
}

fun List<String>.toCave(): Cave = this.flatMap {
    it.split(" -> ").asSequence()
        .map(Coordinate::of)
        .zipWithNext()
        .map { (a, b) -> a.coordsBetween(b) }
        .flatten()
}.associateWith { Material.Rock }