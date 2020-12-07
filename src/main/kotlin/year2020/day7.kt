package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import utils.collections.keysWhereValue
import utils.collections.repeated
import utils.hof.memoized

fun main() {
    val bagRuler = BagHelper(readLinesFromFile("2020/day7.txt"))

    bagRuler.bagsThatCanContain("shiny gold").also { displayPart1(it.size) }
    bagRuler.bagsInsideTheBag("shiny gold").also { displayPart2(it.size) }


    // and now with real bags...
    val bagFactory = BagFactory(readLinesFromFile("2020/day7.txt"))
    val allBags = bagFactory.bagInventory().map { bagFactory.packMyBag(it) }

    allBags.count { it.containsBag("shiny gold") }.also(displayPart1)

    val shinyGoldBag = bagFactory.packMyBag("shiny gold")
    shinyGoldBag.countContents().also(displayPart2)

    println(shinyGoldBag)

}

class BagHelper(inputs: List<String>) {

    private val parsedInput: Map<String, List<NameAndNumber>> = inputs.map { parse(it) }.toMap()

    fun bagsThatCanContain(bagName: String): Set<String> =
        parsedInput.keysWhereValue { it.any { (name) -> name == bagName }}
            .flatMap { bagsThatCanContain(it).plus(it) }
            .toSet()

    fun bagsInsideTheBag(bagName: String): List<String> =
        parsedInput.getOrDefault(bagName, emptyList())
            .flatMap { (bagsInsideTheBag(it.name).plus(it.name)).repeated(it.number) }
}

class BagFactory(inputs: List<String>) {

    private val parsedInput: Map<String, List<NameAndNumber>> = inputs.map { parse(it) }.toMap()

    private val cachedBags = mutableMapOf<String, Bag>() // heap cant cope without this. .·´¯`(>·<)´¯`·. no real bags for us

    // would be good to declare this using memoized like in Bag.containsBag
    // it cant cope with the recursion because the value isn't defined yet
    // Im getting away with it in Bag because its "recursing" through different objects
    // PR's welcome
    fun packMyBag(name: String): Bag {
        return cachedBags.getOrPut(name)
        { Bag(
            name,
            parsedInput.getOrDefault(name, emptyList())
                .flatMap { (contentName, number) -> (1..number).map { packMyBag(contentName) } }
        ) }
    }

    fun bagInventory() = parsedInput.keys

}

data class Bag(val name: String, val contents: List<Bag>) {

    // takes a looooooooong time without this
    val containsBag: (String) -> Boolean = memoized { nameToFind -> contents.any { it.name == nameToFind || it.containsBag(nameToFind) } }

    fun countContents(): Int = contents.sumBy { 1 + it.countContents() }
    override fun toString(): String {
        val sb = StringBuilder()
        _toString(sb, 0)
        return sb.toString()
    }

    private fun _toString(sb: StringBuilder, indent: Int) {
        repeat(indent) { sb.append("\t")}
        sb.append(name).append("\n")
        contents.forEach { it._toString(sb, indent + 1) }
    }
}

data class NameAndNumber(val name: String, val number: Int)

val numberAndName = """(\d+) ([a-z]+ [a-z]+) bag.*""".toRegex()
fun parse(input: String) =
    input.split(" bags contain ")
        .let { (name, rest) ->
            name to
            listOf(rest)
                .filter { it != "no other bags." }
                .flatMap { it.split(", ") }
                .map { numberAndName.matchEntire(it)?.destructured ?: throw RuntimeException(it) }
                .map {(number, name) -> NameAndNumber(name, number.toInt())}
        }