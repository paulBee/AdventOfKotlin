package utils.hof

fun <X, R> memoized(fn: (X) -> R): (X) -> R {
    val cache: MutableMap<X, R> = HashMap()
    return {
        cache.getOrPut(it, { fn(it) })
    }
}

fun <T> oneOf(vararg options: T): (T) -> Boolean = { it in options }
fun <T, R> firstArg(fn: (T) -> R): (T, Any) -> R = { it, _ -> fn(it) }