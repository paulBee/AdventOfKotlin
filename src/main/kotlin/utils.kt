import java.io.File

fun readLinesFromFile(fileName: String): List<String> = readFile(fileName).readLines()

fun readTextFromFile(fileName: String): String = readFile(fileName).readText()

private fun readFile(fileName: String) = File("src/main/resources/$fileName")