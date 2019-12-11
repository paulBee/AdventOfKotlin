package misc

enum class COLOUR {
    BLACK, WHITE;

    fun toLong() =
        when (this) {
            BLACK -> 0L
            WHITE -> 1L
        }

    fun displayChar() =
        when (this) {
            BLACK -> " "
            WHITE -> "#"
        }
}