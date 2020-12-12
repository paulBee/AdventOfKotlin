package utils.types

sealed class Either <S,T> (val left: S?, val right: T?) {
    abstract fun asRight(): Right<S, T>?
}
data class Left<S, T>(val value: S): Either<S, T>(value, null) {
    override fun asRight() = null

}
data class Right<S, T>(val value: T): Either<S, T>(null, value) {
    override fun asRight() = this
}