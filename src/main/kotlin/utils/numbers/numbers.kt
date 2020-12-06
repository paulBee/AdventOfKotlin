import kotlin.math.pow

infix fun Int.pow(exponent: Int) = this.toDouble().pow(exponent).toInt()