package utils.types

sealed class Either <S,T> (val left: S?, val right: T?) {}
data class Left<S, T>(val value: S): Either<S, T>(value, null) {}
data class Right<S, T>(val value: T): Either<S, T>(null, value) {}