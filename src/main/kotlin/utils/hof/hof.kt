package utils.hof

fun <X, R> memoized(fn: (X) -> R): (X) -> R {
    val cache: MutableMap<X, R> = HashMap()
    return {
        cache.getOrPut(it, { fn(it) })
    }
}