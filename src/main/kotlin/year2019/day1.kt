package year2019

import readLinesFromFile

fun fuelForMass(mass: Int): Int =
    maxOf(
        (mass / 3) - 2,
        0
    )

fun fuelForMassAndFuel(mass: Int): Int =
    generateSequence(mass) { fuelForMass(it) }
        .drop(1)
        .takeWhile { it > 0 }
        .sum()

fun main() {
    val masses = listOfMasses()
    println("fuel for just module mass: ${masses.sumBy { fuelForMass(it) }}")
    println("fuel for modules and fuel mass: ${masses.sumBy { fuelForMassAndFuel(it) }}")
}

fun listOfMasses(): List<Int> = readLinesFromFile("2019/day1.txt").map { it.toInt() }