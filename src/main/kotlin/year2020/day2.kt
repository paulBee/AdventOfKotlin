package year2020

import displayAnswer
import displayPart1
import displayPart2
import readLinesFromFile

fun main() {
    val parsedInput = readLinesFromFile("2020/day2.txt").map(String::parse)

    parsedInput.count { (data, password) -> password.conformsTo(MinMaxPolicy(data)) }.also(displayPart1)
    parsedInput.count { (data, password) -> password.conformsTo(XORPolicy(data)) }.also(displayPart2)
}

typealias Password = String

interface PasswordPolicy {
    fun check(password: Password): Boolean
}

class MinMaxPolicy(val data: PolicyData): PasswordPolicy {
    override fun check(password: Password) = password.count {it == data.char } in data.firstNumber..data.secondNumber
}

class XORPolicy(val data: PolicyData): PasswordPolicy {
    override fun check(password: Password) =
        (password[data.firstNumber - 1] == data.char) xor (password[data.secondNumber - 1] == data.char)
}

fun Password.conformsTo(policy: PasswordPolicy) = policy.check(this)

data class PolicyData(val firstNumber: Int, val secondNumber: Int, val char: Char)

val inputRegex = """(\d+)-(\d+) ([a-z]): (\w+)""".toRegex()
fun String.parse(): Pair<PolicyData, Password> {
    val (min, max, char, password) = inputRegex.matchEntire(this)
        ?.destructured
        ?: throw IllegalArgumentException("String $this does not match regex")
    return Pair(PolicyData(min.toInt(), max.toInt(), char.first()), password)
}