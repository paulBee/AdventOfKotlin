package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile

fun main() {

    simulation(Point3D.initial).take(6).last().also { grid -> displayPart1(grid.values.count { it }) }
    simulation(Point4D.initial).take(6).last().also { grid -> displayPart2(grid.values.count { it }) }
}


private fun <T: Point<T>> simulation(initial: Map<T, Boolean>) =
    generateSequence(initial) { lastGrid ->
        lastGrid.expandHorizon()
            .map { (point, isActive) ->
                point to when(point.activeNeighbours(lastGrid)) {
                        3 -> true
                        2 -> isActive
                        else -> false
                    }
            }
            .toMap()
    }.drop(1)

fun <T: Point<T>> Map<T, Boolean>.expandHorizon(): Map<T, Boolean> =
    this.keys.flatMap { it.neighbours() }.distinct()
        .map { it to this.getOrDefault(it, false)}
        .toMap()

interface Point<T: Point<T>> {
    fun neighbours(): List<T>

    fun activeNeighbours(grid: Map<T, Boolean>) =
        neighbours().count { grid.getOrDefault(it, false) }
}

data class Point3D(val x: Int, val y: Int, val z: Int): Point<Point3D> {
    override fun neighbours() : List<Point3D> =
        (-1..1).flatMap { dx ->
            (-1..1).flatMap { dy ->
                (-1..1).map { dz ->
                    Point3D(x + dx, y + dy, z + dz)
                }
            }
        }.filter { it != this }

    companion object {
        val initial = readLinesFromFile("2020/day17.txt").reversed()
            .flatMapIndexed { y, row -> row.mapIndexed { x, char -> Point3D(x, y, 0) to (char == '#') } }
            .toMap()
    }
}

data class Point4D(val x: Int, val y: Int, val z: Int, val t: Int): Point<Point4D> {

    override fun neighbours() : List<Point4D> =
        (-1..1).flatMap { dx ->
            (-1..1).flatMap { dy ->
                (-1..1).flatMap { dz ->
                    (-1..1).map { dt ->
                        Point4D(x + dx, y + dy, z + dz, t + dt)
                    }
                }
            }
        }.filter { it != this }

    companion object {
        val initial = readLinesFromFile("2020/day17.txt").reversed()
            .flatMapIndexed { y, row -> row.mapIndexed { x, char -> Point4D(x, y, 0, 0) to (char == '#') } }
            .toMap()
    }
}