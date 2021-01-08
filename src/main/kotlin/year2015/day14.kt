package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.chunkWhen

fun main() {
    val deer = readLinesFromFile("2015/day14.txt").map { Reindeer.from(it) }

    repeat(2503) { deer.forEach(Reindeer::mush) }

    deer.maxByOrNull { it.distance }?.also { displayPart1(it.distance) }

    deer.forEach { it.returnToStart() }

    val scoreboard = deer.map { it to 0 }.toMap().toMutableMap()

    repeat(2503) {
        deer.forEach(Reindeer::mush)

        deer.sortedByDescending { it.distance }
            .chunkWhen { deer1, deer2 -> deer1.distance != deer2.distance }
            .first()
            .forEach(scoreboard::score)
    }

    scoreboard.values.maxOrNull()?.also(displayPart2)
}

private fun MutableMap<Reindeer,Int>.score(deer: Reindeer) {
    this[deer] = this[deer]!! + 1
}

private class Reindeer(val name: String, val speed: Long, val stamina: Long, val recovery: Long) {

    var distance = 0L
    var time = 0

    val cycle = stamina + recovery

    fun mush() {
        if (time % cycle < stamina) distance += speed
        time++
    }

    fun returnToStart() {
        distance = 0L
        time = 0
    }

    companion object {
        val regex = "(\\w+) can fly (\\d+) km/s for (\\d+) seconds, but then must rest for (\\d+) seconds.".toRegex()
        fun from(string: String): Reindeer =
           regex.matchEntire(string)?.destructured
               ?.let { (name, speed, stamina, recovery) -> Reindeer(name, speed.toLong(), stamina.toLong(), recovery.toLong())}
               ?: throw RuntimeException("BOOM! $string")
    }
}