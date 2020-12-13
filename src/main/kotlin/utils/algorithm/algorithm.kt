package utils.algorithm

fun highestCommonFactor(number1: Int, number2: Int): Int =
    if (number2 != 0)
        highestCommonFactor(number2, number1 % number2)
    else
        number1

fun highestCommonFactor(number1: Long, number2: Long): Long =
    if (number2 != 0L)
        highestCommonFactor(number2, number1 % number2)
    else
        number1

fun lowestCommonMultiple(number1: Long, number2: Long): Long =
    (number1 * number2) / highestCommonFactor(number1, number2)