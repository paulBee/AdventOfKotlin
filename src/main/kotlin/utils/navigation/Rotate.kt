package utils.navigation

sealed class Rotate {
    object Left: Rotate() {}
    object Right: Rotate() {}
}