import kotlin.math.pow

infix fun Int.pow(exponent: Int) = this.toDouble().pow(exponent).toInt()

fun Int.isOdd() = !this.isEven()
fun Int.isEven() = this % 2 == 0

fun Pair<Number, Number>.isIncreasing() = this.second.toDouble() > this.first.toDouble()
fun Pair<Number, Number>.isDecreasing() = !this.isIncreasing()