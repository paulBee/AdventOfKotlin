package year2016

import utils.aoc.displayPart1
import utils.aoc.readLinesFromFile
import utils.navigation.Coordinate

val width = 50
val height = 6
val RECT_INST = "rect "
val ROW_INST = "rotate row y="
val COL_INST = "rotate column x="


fun main() {
    val screen = (0 until width)
        .flatMap { x -> (0 until height).map { y -> Coordinate(x,y) to false }}
        .toMap().toMutableMap()

    readLinesFromFile("2016/day8.txt").forEach { line ->
      when {
          line.startsWith(RECT_INST) -> screen.rectangleOn(line.drop(RECT_INST.length))
          line.startsWith(ROW_INST) -> screen.rotateRow(line.drop(ROW_INST.length))
          line.startsWith(COL_INST) -> screen.rotateColumn(line.drop(COL_INST.length))
          else -> throw RuntimeException("piss poor parsing of ${line}")
      }
    }

    screen.values.count { it }.also(displayPart1)

    (0 until height).forEach { y ->
        screen.entries.filter { it.key.y == y }
            .sortedBy { it.key.x }
            .joinToString("") { if(it.value) "#" else "." }
            .also(::println)
    }

}

fun MutableMap<Coordinate, Boolean>.rotateColumn(instr: String) {
    val (index, by) = parseBy(instr)
    val updates = this.entries
        .filter { it.key.x == index }
        .map { (key, value) -> Coordinate(key.x, (key.y + by) % height ) to value }

    updates.forEach { (key, value) -> this[key] = value }
}

fun MutableMap<Coordinate, Boolean>.rotateRow(instr: String) {
    val (index, by) = parseBy(instr)
    val updates = this.entries
        .filter { it.key.y == index }
        .map { (key, value) -> Coordinate((key.x + by) % width, key.y) to value }

    updates.forEach { (key, value) -> this[key] = value }
}

fun MutableMap<Coordinate, Boolean>.rectangleOn(instr: String) {
    val (dx, dy) = parseX(instr)
    (0 until dx)
        .flatMap { x -> (0 until dy).map { y -> Coordinate(x, y) } }
        .forEach { this[it] = true }
}

fun parseBy(string: String) =
    string.split(" by ")
        .map { it.toInt() }

fun parseX(string: String) =
    string.split("x")
        .map { it.toInt() }