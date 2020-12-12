import utils.navigation.Left
import utils.navigation.Right
import utils.navigation.Rotation

interface Circular<T> {
    val next: T
    val previous: T

    fun rotate(rotation: Rotation) = when (rotation) {
        Left -> previous
        Right -> next
        else -> throw RuntimeException("I cant twist that way $rotation")
    }
}