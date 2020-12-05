package utils.algorithm

fun highestCommonFactor(number1: Int, number2: Int): Int =
    if (number2 != 0)
        highestCommonFactor(number2, number1 % number2)
    else
        number1