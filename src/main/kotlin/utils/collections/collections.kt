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

fun <T> Sequence<T>.takeWhileInclusive(pred: (T) -> Boolean): Sequence<T> {
    var previousResult = true
    return takeWhile {
        val shouldStop = previousResult
        pred(it).also { thisResult -> previousResult = thisResult }
        shouldStop
    }
}

/**
 * produces the product of a list of numbers, converts them to Long to reduce overflow issues
 */
fun List<Number>.multiply() =
    this.map { it.toLong() }
        .reduce { acc, i -> acc * i  }


fun <T> List<T>.sumLongBy(fn: (T) -> Long): Long = this.map{ fn(it) }.fold(0L) { acc, next -> acc + next }

/**
 * iterates through the list providing each element and the remaining tail.
 */
fun <T> List<T>.scanHeadAndTail() =
    generateSequence(this.headAndTail()) { (_, tail) -> tail.headAndTail() }
        .takeWhile { (_, tail) -> tail.isNotEmpty() }

fun <T> List<T>.headAndTail() = Pair(this.first(), this.drop(1))

/**
 * divide a list down the middle
 */
fun <T> List<T>.bifurcate() =
    if (this.size % 2 != 0) throw RuntimeException("how odd! there seems to be a spare")
    else Pair(this.take(this.size / 2), this.drop(this.size /2))

/**
 * produces the list repeated the specified number of times
 */
fun <T> List<T>.repeated(number: Int): List<T> =
    (1..number).fold(listOf<T>()) { acc, _ -> acc.plus(this) }

/**
 * returns all keys where the value matches a predicate
 */
fun <K,V> Map<K,V>.keysWhereValue(predicate: (V) -> Boolean) =
    this.filter { (_, value) -> predicate(value) }
        .map { (key) -> key }

fun <K,V> Map<K, List<V>>.occurrencesOf(number: K) = this[number]?.size ?: 0