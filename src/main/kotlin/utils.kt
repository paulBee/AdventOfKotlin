import java.io.File

fun readLinesFromFile(fileName: String): List<String> = readFile(fileName).readLines()

fun readTextFromFile(fileName: String): String = readFile(fileName).readText()

private fun readFile(fileName: String) = File("src/main/resources/$fileName")


//List fun
/**
 * combines 2 iterables into an iterable of Pairs with every combination of the two arguments
 */
fun <S, T> productOf(iter1 : Iterable<S>, iter2: Iterable<T>) : Iterable<Pair<S, T>> =
    iter1.flatMap{ s ->
        iter2.map { t -> Pair(s, t) }
    }

/**
 * chunks a list in to sub lists every time adjacent elements pass the supplied check
 */
fun <T> List<T>.chunkWhen(newChunkWhenTrue: (T, T) -> Boolean): List<List<T>> {
    return this.fold(mutableListOf<MutableList<T>>()) { chunks, next ->
        if (chunks.lastOrNull()?.let { newChunkWhenTrue.invoke(it.last(), next) } == true) {
            chunks.last().add(next)
            chunks
        } else {
            chunks += mutableListOf(next)
            chunks
        }
    }
}