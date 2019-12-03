import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class Day3Tests {

    @Test
    fun `example 1`() {
        val wire1 = buildWire(inputLineToMoveInstructions("R75,D30,R83,U83,L12,D49,R71,U7,L72"))
        val wire2 = buildWire(inputLineToMoveInstructions("U62,R66,U55,R34,D71,R55,D58,R83"))

        assertThat(nearestManhattanDistance(findIntersections(wire1, wire2))).isEqualTo(159)
    }

    @Test
    fun `example 2`() {
        val wire1 = buildWire(inputLineToMoveInstructions("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51"))
        val wire2 = buildWire(inputLineToMoveInstructions("U98,R91,D20,R16,D67,R40,U7,R15,U6,R7"))

        assertThat(nearestManhattanDistance(findIntersections(wire1, wire2))).isEqualTo(135)
    }

}