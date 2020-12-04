package year2020

import chunkWhen
import displayPart1
import displayPart2
import readLinesFromFile

fun main() {
    val passports = readLinesFromFile("2020/day4.txt")
        .chunkWhen { _, element -> element == "" }
        .map { passportData ->
            passportData.filter { it != "" }
                .flatMap { it.split(" ") }
                .map { it.parseAttribute() }
                .let { Passport(it) }
        }

    passports.count { it.containsAllMandatory() }.also(displayPart1)
    passports.count { it.isValid() }.also(displayPart2)
}


class Passport(attributes: List<PassportAttribute>) {

    private val attrMap = attributes.map { it.name to it.data }.toMap()
    private val attributeValidation = listOf(
        AttributeValidator("byr", true, isPresentAnd(aNumberBetween(1920, 2002))), // (Birth Year)
        AttributeValidator("iyr", true, isPresentAnd(aNumberBetween(2010, 2020))), // (Issue Year)
        AttributeValidator("eyr", true, isPresentAnd(aNumberBetween(2020, 2030))), // (Expiration Year)
        AttributeValidator("hgt", true, isPresentAnd(::validHeight)), // (Height)
        AttributeValidator("hcl", true, isPresentAnd(passesRegex("#[0-9a-f]{6}"))), // (Hair Color)
        AttributeValidator("ecl", true, isPresentAnd(passesRegex("(amb|blu|brn|gry|grn|hzl|oth)"))), // (Eye Color)
        AttributeValidator("pid", true, isPresentAnd(passesRegex("[0-9]{9}"))), // (Passport ID)
        AttributeValidator("cid", false, anyValue), // (Country ID)
    )

    fun containsAllMandatory() = attributeValidation.
    all { (name, mandatory, _) -> attrMap.containsKey(name) || !mandatory }

    fun isValid() = attributeValidation
        .all { (name, mandatory, predicate) -> attrMap[name]?.let { predicate(it) } ?: !mandatory }
}

fun isPresentAnd(predicate: (String) -> Boolean): (String?) -> Boolean {
    return { it?.let(predicate) ?: false }
}

fun aNumberBetween(min: Long, max: Long): (String) -> Boolean {
    return { it.toIntOrNull()?.let { int -> int in min..max } ?: false}
}

fun validHeight(it: String): Boolean {
    val units = it.takeLast(2)
    val value = it.take(it.length - 2)
    return when (units) {
        "cm" -> aNumberBetween(150, 193)(value)
        "in" -> aNumberBetween(59, 76)(value)
        else -> false
    }
}

fun passesRegex(str: String): (String) -> Boolean {
    val regex = str.toRegex()
    return { regex.matches(it) }
}

val anyValue = { _: String -> true }

data class AttributeValidator(val name: String, val mandatory: Boolean, val validPredicate: (String) -> Boolean)

data class PassportAttribute(val name: String, val data: String)

fun String.parseAttribute(): PassportAttribute {
    val bits = this.split(':')
    if (bits.size != 2) {
        throw RuntimeException("Bad Attribute $this")
    }
    return PassportAttribute(bits[0], bits[1])

}