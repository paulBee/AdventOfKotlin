import intcodeComputers.Computer
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class Day2Tests {

    @Test
    fun `run example program`() {
        val computer = Computer(listOf(1,9,10,3,2,3,11,0,99,30,40,50))
        assertThat(computer.runProgram(9, 10)).isEqualTo(3500)
    }

    @Test
    fun `simple add program`() {
        val computer = Computer(listOf(1,0,0,0,99))
        assertThat(computer.runProgram(0, 0)).isEqualTo(2)
    }

    @Test
    fun `simple multiply program`() {
        val computer = Computer(listOf(2,5,0,0,99,3))
        assertThat(computer.runProgram(5, 0)).isEqualTo(6)
    }

    @Test
    fun `two iteration program`() {
        val computer = Computer(listOf(1,1,1,4,99,5,6,0,99))
        assertThat(computer.runProgram(1, 1)).isEqualTo(30)
    }

}
