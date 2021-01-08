package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile

fun main() {

    val medicineMolecule = readLinesFromFile("2015/day19.txt").last()


    MoleculeMachine.synthesize(medicineMolecule)
        .first().also { displayPart1(it.size) }

    (medicineMolecule.count { it.isUpperCase() } -
    medicineMolecule.windowed(2).count { it in listOf("Ar", "Rn") } -
    (2 * medicineMolecule.count { it == 'Y'}) -
    1)
        .also(displayPart2)

}

object MoleculeMachine {

    private val replacements = readLinesFromFile("2015/day19.txt").takeWhile { it.isNotBlank() }
        .map { it.split(" => ") }
        .map { (match, replacement) -> match to replacement}

    private fun forwards(molecule: String, replacement: Pair<String, String>) =
        molecule.splits()
            .filter { (_, end) -> end.startsWith(replacement.first) }
            .map { (start, end) -> "$start${end.replaceFirst(replacement.first, replacement.second)}"}

    fun synthesize(molecule: String) =
        generateSequence(setOf(molecule)) { molecules -> molecules.flatMap { m -> replacements.flatMap { forwards(m, it) } }.toSet() }
            .drop(1)
}


private fun String.splits() = this.indices.map { this.take(it) to this.drop(it)}