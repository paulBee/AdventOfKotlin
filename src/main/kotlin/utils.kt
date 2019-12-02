import java.io.File

fun readLinesFromFile(fileName: String): List<String> = readFile(fileName).readLines()

fun readTextFromFile(fileName: String): String = readFile(fileName).readText()

private fun readFile(fileName: String) = File("src/main/resources/$fileName")

fun <S, T> productOf(iter1 : Iterable<S>, iter2: Iterable<T>) : Iterable<Pair<S, T>> =
    iter1.flatMap{ s ->
        iter2.map { t -> Pair(s, t) }
    }