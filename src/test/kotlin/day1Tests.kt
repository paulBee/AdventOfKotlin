import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day1Tests {

    @ParameterizedTest
    @MethodSource("simpleMassAndFuel")
    fun `assert mass to fuel ratio`(data: MassAndExpectedFuel) {
        val (mass, expectedFuel) = data
        assertThat(fuelForMass(mass)).isEqualTo(expectedFuel)
    }

    @ParameterizedTest
    @MethodSource("recursiveMassAndFuel")
    fun `assert fuel correct when taking in to account mass of fuel`(data: MassAndExpectedFuel) {
        val (mass, expectedFuel) = data
        assertThat(fuelForMassAndFuel(mass)).isEqualTo(expectedFuel)
    }

    companion object {
        @JvmStatic
        private fun simpleMassAndFuel() = Stream.of(
            MassAndExpectedFuel(mass = 12, expectedFuel = 2),
            MassAndExpectedFuel(mass = 14, expectedFuel = 2),
            MassAndExpectedFuel(mass = 1969, expectedFuel = 654),
            MassAndExpectedFuel(mass = 100756, expectedFuel = 33583)
        )

        @JvmStatic
        private fun recursiveMassAndFuel() = Stream.of(
            MassAndExpectedFuel(mass = 12, expectedFuel = 2),
            MassAndExpectedFuel(mass = 14, expectedFuel = 2),
            MassAndExpectedFuel(mass = 1969, expectedFuel = 966),
            MassAndExpectedFuel(mass = 100756, expectedFuel = 50346)
        )
    }

    data class MassAndExpectedFuel(val mass: Int, val expectedFuel: Int)
}
