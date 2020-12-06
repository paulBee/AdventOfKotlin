package utils.aoc

import java.io.File
import java.io.Serializable
import kotlin.random.Random

fun readLinesFromFile(fileName: String): List<String> = readFile(fileName).readLines()

fun readTextFromFile(fileName: String): String = readFile(fileName).readText()

fun readProgramInstructions(fileName: String) = readTextFromFile(fileName).split(",").map { it.toLong() }

private fun readFile(fileName: String) = File("src/main/resources/$fileName")

enum class AocPart(val display: String) {
    PART1 ("Part 1"),
    PART2 ("Part 2")
}

fun displayAnswer(part: AocPart): (Serializable) -> Unit {
    return { answer -> println("Answer for ${part.display}: $answer") }
}

val displayPart1 = displayAnswer(AocPart.PART1)
val displayPart2 = displayAnswer(AocPart.PART2)

fun <T> pickRandom (vararg options: T) = options[Random.nextInt(0, options.size)]