package utils.collections

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

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
        val shouldSContinue = previousResult
        previousResult = pred(it)
        shouldSContinue
    }
}

fun <T> List<T>.takeWhileInclusive(pred: (T) -> Boolean): List<T> = this.asSequence().takeWhileInclusive(pred).toList()

fun <T> Sequence<T>.untilStable(): T = this.takeUntilStable().last()

fun <T> Sequence<T>.takeUntilStable(): Sequence<T> =
    this.zipWithNext()
        .takeWhileInclusive { (a, b) -> a != b }
        .map { (it, _) -> it }

/**
 * produces the product of a list of numbers, converts them to Long to reduce overflow issues
 */
fun List<Number>.multiply() =
    this.map { it.toLong() }
        .reduce { acc, i -> acc * i  }


fun Collection<Long>.sumLong(): Long = this.sumLongBy { it }
fun <T> Collection<T>.sumLongBy(fn: (T) -> Long): Long = this.map{ fn(it) }.fold(0L) { acc, next -> acc + next }

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

fun <T> PersistentList<T>.dropP(amount: Int): PersistentList<T> = this.drop(amount).toPersistentList()
fun <T> List<T>.takeP(amount: Int) = this.take(amount).toPersistentList()

fun <T> List<T>.windowedWithTail(windowSize: Int): Sequence<Pair<List<T>, List<T>>> {
    var list = this.toPersistentList()
    return sequence { while(list.size >= windowSize) {
        yield(list.takeP(windowSize) to list.dropP(windowSize))
        list = list.dropP(1)
    } }
}

val <E> Collection<E>.arrangements: List<List<E>>
    get() {
        return when (this.size) {
            1 -> listOf(this.toList())
            else -> this.flatMap { x -> (this - x).arrangements.map { listOf(x) + it } }
        }
    }

fun <E> Collection<E>.modalEntry() = this.groupBy { it }.entries.maxBy { it.value.size }!!.key

fun <E> List<E>.firstDuplicate(): E? {
    this.scan(emptyList<E>()) { acc, next ->
        if (acc.contains(next)) return next
        acc + next
    }
    return null
}

fun List<Number>.windowAverage(windowSize: Int) =
    this.windowed(windowSize)
        .map { window -> window.sumOf { it.toDouble() } / windowSize }

fun <T>List<T>.pairedWithNext(): List<Pair<T, T>> =
    this.windowed(2)
        .map { Pair(it[0], it[1]) }