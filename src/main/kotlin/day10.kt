import coOrdinates.Coordinate
import coOrdinates.DirectionRatio
import java.lang.IllegalStateException

fun main () {
    val fileLines = readLinesFromFile("day10.txt")

    val ySize = fileLines.size
    val xSize = fileLines[0].length

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

    val coordsToType = fileLines
        .mapIndexed { y, line -> line.mapIndexed { x, char -> Pair(Coordinate(x, y), char.toString().roidOrVoid()) }  }
        .flatten()
        .fold(HashMap<Coordinate, TYPE>()) { acc, pair -> acc[pair.first] = pair.second; acc }

    val max = coordsToType.keys
        .map { countRoids(it, coordsToType, allDirections) }
        .max()

    println(max)


}

fun countRoids(point: Coordinate, roidMap: RoidMap, directions: List<DirectionRatio>) =
    directions
        .count { asteroidInDirection(point, roidMap, it) }


typealias RoidMap = Map<Coordinate, TYPE>



fun asteroidInDirection(point: Coordinate, roidMap: RoidMap, direction: DirectionRatio) =
    generateSequence(point) { it.follow(direction) }
        .drop(1) // dont include yourself
        .takeWhile { roidMap.containsKey(it) }
        .toList()
        .any { roidMap[it] == TYPE.ROID }


enum class TYPE { ROID, VOID }

fun String.roidOrVoid () : TYPE =
    when (this) {
        "#" -> TYPE.ROID
        "." -> TYPE.VOID
        else -> throw IllegalStateException("ROID RAGE!!! I dont know what this is $this")
    }