package year2022

import utils.aoc.readLinesFromFile
import utils.collections.takeWhileInclusive
import utils.navigation.Coordinate
import utils.navigation.orthogonalSequences

fun main() {
    val input = readLinesFromFile("2022/day8.txt")


    val forest = input.foldIndexed(mutableMapOf<Coordinate, Tree>()) { y, forest, line ->
        val trees = line.mapIndexed { x, char -> Tree(char.toInt(), Coordinate(x, y), forest) }
        trees.forEach { forest[it.coordinate] = it }
        forest
    }

    println(forest.values.count { it.isVisible() })
    println(forest.values.maxOf { it.viewingScore() })

}

class Tree(val height: Int, val coordinate: Coordinate, val forest: Map<Coordinate, Tree>) {

    fun isHigherThan(tree: Tree) = tree.height < height

    fun isVisible() = treesInAllDirs().any { treeSeq -> treeSeq.all(this::isHigherThan) }

    fun viewingScore() = treesInAllDirs().map { treeSeq -> treeSeq.takeWhileInclusive(this::isHigherThan).count() }
        .reduce(Int::times)

    private fun treesInAllDirs(): List<Sequence<Tree>> {
        return orthogonalSequences(coordinate)
            .map { coordinateSeq -> coordinateSeq.takeWhile { forest[it] != null }.mapNotNull { forest[it] } }
    }


}