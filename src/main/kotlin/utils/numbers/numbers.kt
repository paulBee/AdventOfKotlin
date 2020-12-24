import kotlin.math.pow

infix fun Int.pow(exponent: Int) = this.toDouble().pow(exponent).toInt()

fun Int.isOdd() = !this.isEven()
fun Int.isEven() = this % 2 == 0