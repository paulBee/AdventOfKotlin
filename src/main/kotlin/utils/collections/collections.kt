package utils.collections

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

/**
 * iterates through the list providing each element and the remaining tail.
 */
fun <T> List<T>.scanHeadAndTail() =
    generateSequence(this.headAndTail()) { (_, tail) -> tail.headAndTail() }
        .takeWhile { (_, tail) -> tail.isNotEmpty() }

fun <T> List<T>.headAndTail() = Pair(this.first(), this.drop(1))