fun fuelForMass(mass: Int): Int =
    maxOf(
        (mass / 3) - 2,
        0
    )

fun fuelForMassAndFuel(mass: Int): Int =
    generateSequence(mass, ::fuelForMass)
        .drop(1)
        .takeWhile { it > 0 }
        .sum()

fun main() {
    val masses = listOfMasses()
    println("fuel for just module mass: ${masses.sumBy(::fuelForMass)}")
    println("fuel for modules and fuel mass: ${masses.sumBy(::fuelForMassAndFuel)}")
}

fun listOfMasses(): List<Int> = readLinesFromFile("day1.txt").map(String::toInt)