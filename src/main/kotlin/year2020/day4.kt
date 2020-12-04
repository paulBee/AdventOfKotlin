package year2020

import chunkOnEmptyLine
import displayPart1
import displayPart2
import readLinesFromFile

fun main() {
    val passports = readLinesFromFile("2020/day4.txt").chunkOnEmptyLine()
        .map { passportData -> passportData.flattenWhitespace().map { it.parseAttribute() } }
        .map { Passport(it) }

    passports.count { it.containsAllMandatory() }.also(displayPart1)
    passports.count { it.isValid() }.also(displayPart2)
}

class Passport(private val attributes: List<PassportAttribute>, private val mandatoryAttrs: List<String> = defaultMandatory) {
    fun containsAllMandatory() = mandatoryAttrs.all { attr -> attributes.any { attr == it.name } }
    fun isValid() = containsAllMandatory() && attributes.all { it.isValid() }
}

val defaultMandatory = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")

data class PassportAttribute(val name: String, val data: String) {
    fun isValid() =
        when(name) {
            "byr" -> validBirthYear(data)
            "iyr" -> validIssueYear(data)
            "eyr" -> validExpiryYear(data)
            "hgt" -> validHeight(data)
            "hcl" -> validHairColour(data)
            "ecl" -> validEyeColour(data)
            "pid" -> validPassportId(data)
            "cid" -> true
            else -> false
        }
}

val validBirthYear = aNumberBetween(1920, 2002)
val validIssueYear = aNumberBetween(2010, 2020)
val validExpiryYear = aNumberBetween(2020, 2030)
val validHairColour = Regex("#[0-9a-f]{6}")::matches
val validEyeColour = Regex("(amb|blu|brn|gry|grn|hzl|oth)")::matches
val validPassportId = Regex("[0-9]{9}")::matches

fun validHeight(it: String): Boolean {
    val units = it.takeLast(2)
    val value = it.take(it.length - 2)
    return when (units) {
        "cm" -> aNumberBetween(150, 193)(value)
        "in" -> aNumberBetween(59, 76)(value)
        else -> false
    }
}

fun aNumberBetween(min: Long, max: Long): (String) -> Boolean {
    return { it.toIntOrNull()?.let { int -> int in min..max } ?: false}
}

fun String.parseAttribute(): PassportAttribute {
    val bits = this.split(':')
    if (bits.size != 2) {
        throw RuntimeException("Bad Attribute $this")
    }
    return PassportAttribute(bits[0], bits[1])

}

fun List<String>.flattenWhitespace() = this.flatMap { it.split(" ") }