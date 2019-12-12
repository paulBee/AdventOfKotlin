import java.lang.IllegalStateException
import java.lang.Math.abs

fun main() {
    part1()
    part2()
}

private fun part1() {
    val moons = readLinesFromFile("day12.txt")
        .map { it.toVec3D() }
        .mapIndexed { index, startingPos -> Moon(index.toMoonName(), startingPos) }

    repeat(1000) {tick(moons) }

    println(moons.sumBy { it.currentEnergy().toInt() })
}

private fun part2() {
    // some suppositions
    // if the system cycles and is deterministic forwards and backwards then the first repeat must be the initial state
    // 3 dimensions operate independently so must return to their initial state (potentially on different cycle times)
    // total system cycles on common multiples of the individual dimension cycle rates
    // therefore cycle time is lowest common multiple of each dimension cycle time
    // lets find out...

    val xFreq = stepsUntil { moons -> !moons.all { moon -> moon.currentVelocity.x == 0L } }
    val yFreq = stepsUntil { moons -> !moons.all { moon -> moon.currentVelocity.y == 0L } }
    val zFreq = stepsUntil { moons -> !moons.all { moon -> moon.currentVelocity.z == 0L } }

    println("$xFreq $yFreq $zFreq ${xFreq.toLong() * yFreq.toLong() * zFreq.toLong()}")

}

private fun stepsUntil(endStateCheck: (List<Moon>) -> Boolean): Int {
    val moons = readLinesFromFile("day12.txt")
        .map { it.toVec3D() }
        .mapIndexed { index, startingPos -> Moon(index.toMoonName(), startingPos) }

    return generateSequence { tick(moons) }
        .takeWhile { endStateCheck.invoke(moons) }
        .count() + 1 // lol dont forget to carry the one
}

fun tick(moons: List<Moon>) {
    moons.forEach { it.applyGravityFrom(moons) }
    moons.forEach { it.move() }
}

private fun Int.toMoonName() =
    when(this) {
        0 -> "Io"
        1 -> "Europa"
        2 -> "Ganymede"
        3 -> "Callisto"
        else -> throw IllegalStateException("That's no moon...")
    }

class Moon(val name: String, startingPosition: Vec3D, startingVelocity: Vec3D = Vec3D(0,0,0)) {
    var currentPosition = startingPosition
    var currentVelocity = startingVelocity

    fun move() {
        currentPosition = currentPosition plus currentVelocity
    }

    fun applyGravityFrom(moons: List<Moon>) {
        val gravityAdjustment = moons
            .map {
                Vec3D (
                    deltaGrav(currentPosition.x, it.currentPosition.x),
                    deltaGrav(currentPosition.y, it.currentPosition.y),
                    deltaGrav(currentPosition.z, it.currentPosition.z)
                )
            }.reduce(Vec3D::plus)
        currentVelocity = currentVelocity plus gravityAdjustment
    }

    fun currentEnergy() = currentPosition.magnitude() * currentVelocity.magnitude()

    private fun deltaGrav(a: Long, b: Long) =
        when {
            a > b -> -1L
            a == b -> 0L
            a < b -> 1L
            else -> throw IllegalStateException("a break in the spacetime continuum")
        }


}

val vectorRegex = """^<x=(.*), y=(.*), z=(.*)>$""".toRegex()
fun String.toVec3D(): Vec3D {
    val (x, y, z) = vectorRegex.matchEntire(this)
        ?.destructured
        ?: throw IllegalArgumentException("String $this does not match regex")
    return Vec3D(x.toLong(), y.toLong(), z.toLong())
}

data class Vec3D(val x: Long, val y: Long, val z: Long) {
    infix fun plus(other: Vec3D) = Vec3D(x + other.x, y + other.y, z + other.z)
    fun magnitude() = abs(x) + abs(y) + abs(z)
}