package year2022

import utils.aoc.readLinesFromFile

fun main() {

    val register = registerSequence(readLinesFromFile("2022/day10.txt"))

    val signalStrength = clockSequence().zip(register).map { (c, r) -> c * r }
    (20..220 step 40).sumOf { signalStrength.elementAt(it - 1) }.also { println(it) }

    // part 2
    val spritePosition = register.map { listOf(it - 1, it, it + 1) }

    pixelSequence().zip(spritePosition)
        .map { (pix, spr) -> if (spr.contains(pix)) '#' else '.' }
        .chunked(40).forEach { println(it.joinToString("")) }

}

fun clockSequence() = generateSequence(1L) { it + 1 }
fun pixelSequence() = generateSequence(0L) { (it + 1) % 40 }.take(40 * 6)

fun registerSequence(lines: List<String>) = sequence<Long> {
    var value = 1L
    lines.forEach { line ->
        when {
            line == "noop" -> yield(value)
            line.startsWith("addx ") -> {
                yield(value)
                yield(value)
                value += line.drop(5).toLong()
            }
        }
    }
}