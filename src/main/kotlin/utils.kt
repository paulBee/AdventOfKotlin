import java.io.File
import java.io.Serializable

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

fun highestCommonFactor(number1: Int, number2: Int): Int =
    if (number2 != 0)
        highestCommonFactor(number2, number1 % number2)
    else
        number1

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
fun <T> List<T>.chunkWhen(newChunkWhenTrue: (T, T) -> Boolean): List<List<T>> =
    this.asSequence().chunkWhen(newChunkWhenTrue).toList()

fun List<String>.chunkOnEmptyLine() =
    this.chunkWhen { _, element -> element.isBlank() }
        .map { chunk -> chunk.filter { it.isNotBlank() } }


fun <T> Sequence<T>.chunkWhen(fn: (T, T) -> Boolean) : Sequence<List<T>> =
    sequence {
        var chunk = mutableListOf<T>()
        for (element in this@chunkWhen) {
            if (chunk.lastOrNull()?.let { fn.invoke(it, element) } == true) {
                yield(chunk)
                chunk = mutableListOf<T>()
            }
            chunk.plusAssign(element)
        }
        yield(chunk)
    }

/**
 * produces the product of a list of numbers, converts them to Long to reduce overflow issues
 */
fun List<Number>.multiply() =
    this.map { it.toLong() }
        .reduce { acc, i -> acc * i  }
