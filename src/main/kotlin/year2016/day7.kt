package year2016

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile

fun main() {

    readLinesFromFile("2016/day7.txt")
        .also { displayPart1(it.count(::supportsTLS)) }
        .also { displayPart2(it.count(::supportsSSL)) }

}

fun supportsTLS(string: String): Boolean {
    var abbaFound = false
    var abbaFoundInBrackets = false

    iterateTrackingBracketDepth(string) { index, bracketDepth ->
        if (index > string.length - 4) {
            return@iterateTrackingBracketDepth
        }

        if (string[index] == string[index+3] &&
            string[index] != string[index+1] &&
            string[index+1] == string[index+2]
        ) {
            abbaFound = true
            if (bracketDepth > 0) {
                abbaFoundInBrackets = true
            }
        }
    }

    return abbaFound && !abbaFoundInBrackets
}



fun supportsSSL(string: String): Boolean {

    val aba = mutableSetOf<Pair<Char,Char>>()
    val bab = mutableSetOf<Pair<Char,Char>>()

    iterateTrackingBracketDepth(string) { index, bracketDepth ->
        if (index > string.length - 3) {
            return@iterateTrackingBracketDepth
        }
        if (string[index] == string[index+2] &&
            string[index] != string[index+1]
        ) {
            if (bracketDepth > 0) {
                aba.add(string[index] to string[index + 1])
            } else {
                bab.add(string[index + 1] to string[index])
            }
        }
    }

    return (aba intersect bab).isNotEmpty()
}

fun iterateTrackingBracketDepth(string: String, fn: (index: Int, bracketDepth: Int) -> Unit) {
    var index = -1
    var bracketDepth = 0
    while (index < string.length - 1) {
        index++
        val char = string[index]
        if (char == '[') {
            bracketDepth++
            continue
        }

        if (char == ']') {
            bracketDepth--
            continue
        }

        fn(index, bracketDepth)
    }
}
