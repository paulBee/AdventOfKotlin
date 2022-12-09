package year2022

import utils.aoc.readLinesFromFile
import utils.navigation.*

fun main() {
    val input = readLinesFromFile("2022/day9.txt")

    val ropeSegmentPaths = generateSequence(input.toCoordPath()) { dragNextSegment(it) }

    println(ropeSegmentPaths.elementAt(1).toSet().size)
    println(ropeSegmentPaths.elementAt(9).toSet().size)

}

fun dragNextSegment(headPath: Sequence<Coordinate>): Sequence<Coordinate> {
    return sequence {
        var latestTailPosition = headPath.first()
        yield(latestTailPosition)
        headPath.drop(1).forEach { headCoord ->

            val drags = listOfNotNull(
                when {
                    headCoord.x - latestTailPosition.x > 1 -> Left
                    headCoord.x - latestTailPosition.x < -1 -> Right
                    else -> null
                },
                when {
                    headCoord.y - latestTailPosition.y > 1 -> Up
                    headCoord.y - latestTailPosition.y < -1 -> Down
                    else -> null
                }
            )

            if (!drags.isEmpty()) {
                latestTailPosition = drags.fold(headCoord) { dragger, direction -> dragger.move(direction) }
                yield(latestTailPosition)
            }
        }
    }
}

fun List<String>.toCoordPath() = this.fold(listOf(Coordinate(0, 0))) { headPath, instruction ->
    val (uldr, distance) = instruction.split(" ")
    headPath + uldr.toDirection().sequenceFrom(headPath.last()).drop(1).take(distance.toInt())
}.asSequence()