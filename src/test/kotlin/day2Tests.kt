import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

@ExperimentalCoroutinesApi
class Day2Tests {

    @Test
    fun `run example program`() {
        val input = listOf(1,9,10,3,2,3,11,0,99,30,40,50).map { it.toLong() }
        assertThat(configureProgram(9, 10, input)).isEqualTo(3500)
    }

    @Test
    fun `simple add program`() {
        val input = listOf(1,0,0,0,99).map { it.toLong() }
        assertThat(configureProgram(0, 0, input)).isEqualTo(2)
    }

    @Test
    fun `simple multiply program`() {
        val input = listOf(2,5,0,0,99,3).map { it.toLong() }
        assertThat(configureProgram(5, 0, input)).isEqualTo(6)
    }

    @Test
    fun `two iteration program`() {
        val input = listOf(1,1,1,4,99,5,6,0,99).map { it.toLong() }
        assertThat(configureProgram(1, 1, input)).isEqualTo(30)
    }

}
