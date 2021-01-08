package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.sumLongBy

fun main() {
    val cityDistances = readLinesFromFile("2015/day9.txt").flatMap { it.toEdges() }
    val cities = cityDistances.flatMap { listOf(it.from, it.to) }.toSet()

    val possibleRoutes = cities.flatMap { it.journeysFrom(cities) }

    val routeDistances = possibleRoutes.map { route ->
        route.zipWithNext().sumLongBy { (from, to) -> cityDistances.first { it.from == from && it.to == to }.distance }
    }

    routeDistances.minOrNull()?.also(displayPart1)
    routeDistances.maxOrNull()?.also(displayPart2)

}

private fun String.journeysFrom(destinations: Set<String>, previously: List<String> = emptyList()): List<List<String>> {
    val journeySofar: List<String> = previously + this
    val toGo = destinations - journeySofar

    return when {
        toGo.isEmpty() -> listOf(journeySofar)
        else -> toGo.flatMap { next -> next.journeysFrom(destinations, journeySofar) }
    }
}

private fun String.toEdges(): List<Edge> {
    val (locations, distance) = this.split(" = ")
    val (from, to) = locations.split(" to ")
    return listOf(
        Edge(from, to, distance.toLong()),
        Edge(to, from, distance.toLong())
    )
}

data class Edge(val from: String, val to: String, val distance: Long)