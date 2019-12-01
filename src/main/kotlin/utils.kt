import java.io.File

fun readLinesFromFile(fileName: String): List<String>
        = File("src/main/resources/$fileName").readLines()