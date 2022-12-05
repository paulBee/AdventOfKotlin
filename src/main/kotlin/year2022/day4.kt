package year2022

import utils.aoc.readLinesFromFile
import java.lang.Integer.min

fun main() {
    val input = readLinesFromFile("2022/day4.txt")

    val ranges = input.map { it.split(",").let { (elf1, elf2) -> elf1.toRange() to elf2.toRange()} }

    ranges.count { (first, second) -> (first.intersect(second).size == min(first.size, second.size)) }
        .also { println(it) }

    ranges.count { (first, second) -> !first.intersect(second).isEmpty() }
        .also { println(it) }


}

fun String.toRange(): Set<Int> {
    val (low, high) = this.split("-")
    return (low.toInt() .. high.toInt()).toSet()
}