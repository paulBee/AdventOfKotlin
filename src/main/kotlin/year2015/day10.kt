package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.collections.chunkWhen

fun main() {
    val sequence = generateSequence("1113122113") { it.sayWhatYouSee() }.drop(1)

    sequence.take(40).also { displayPart1(it.last().length) }
    sequence.take(50).also { displayPart2(it.last().length) }
}

private fun String.sayWhatYouSee() = this.toCharArray().asSequence().chunkWhen{ a, b -> a != b }.joinToString("") { "${it.size}${it.first()}" }

