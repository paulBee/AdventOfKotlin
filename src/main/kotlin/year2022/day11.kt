package year2022

import utils.aoc.readLinesFromFile
import utils.collections.chunkOnEmptyLine
import utils.collections.multiply
import java.lang.RuntimeException

fun main() {

    val monkeys = readLinesFromFile("2022/day11.txt").chunkOnEmptyLine().toMonkeys()

    repeat(20) {
        monkeys.forEach { monkey ->
            while (monkey.hasItem()) {
                monkey.inspectAndThrow()
            }
        }
    }

    println(monkeys.calculateMonkeyBusiness())

    val worrisomeMonkeys = monkeys.evolve()

    repeat(10000) {
        worrisomeMonkeys.forEach { monkey ->
            while (monkey.hasItem()) {
                monkey.inspectAndThrow()
            }
        }
    }

    println(worrisomeMonkeys.calculateMonkeyBusiness())
}

fun List<Monkey>.calculateMonkeyBusiness() = this.map { it.monkeyDiary.size }
    .sorted()
    .takeLast(2)
    .multiply()

open class Monkey (
    val name: Int,
    val operation: Operation,
    val divisibilityTest: Int,
    val monkeyOfTruth: () -> Monkey,
    val monkeyOfLies: () -> Monkey,
    val initialMonkeyList: List<Long>
) {
    val monkeyDiary = mutableListOf<Long>()
    val itemList = ArrayDeque(initialMonkeyList)

    fun inspectAndThrow() =
        itemList.removeFirst()
            .let(operation)
            .let(this::tryToChill)
            .let(this::applyKnowledgeOfModularArithmetic)
            .also { chooseMonkey(it).catch(it) }
            .also { monkeyDiary.add(it) }

    fun chooseMonkey(worryLevel: Long) = if (worryLevel % divisibilityTest == 0L) monkeyOfTruth() else monkeyOfLies()

    fun catch(item: Long) = itemList.addLast(item)

    fun hasItem(): Boolean = itemList.isNotEmpty()

    open fun tryToChill(worry: Long): Long = worry / 3

    open fun applyKnowledgeOfModularArithmetic(worry: Long): Long = worry

}

class WorrisomeMonkey(
    m: Monkey,
    monkeys: List<WorrisomeMonkey>,
    val divisorProduct: Long
): Monkey(
    m.name,
    m.operation,
    m.divisibilityTest,
    { monkeys[m.monkeyOfTruth().name] },
    { monkeys[m.monkeyOfLies().name] },
    m.initialMonkeyList
) {

    override fun tryToChill(worry: Long) = worry
    override fun applyKnowledgeOfModularArithmetic(worry: Long) = worry % divisorProduct
}

private fun List<Monkey>.evolve(): List<WorrisomeMonkey> {
    val divisorProduct = this.discussModularArithmetic()
    return this.fold(mutableListOf<WorrisomeMonkey>()) { monkeys, monkey ->
        monkeys.add(WorrisomeMonkey(monkey, monkeys, divisorProduct))
        monkeys
    }
}

private fun List<Monkey>.discussModularArithmetic() = this.map { it.divisibilityTest }.multiply()

private fun List<List<String>>.toMonkeys() = this
    .fold(mutableListOf<Monkey>()) { monkeys, input ->
        monkeys.add(input.toMonkey(monkeys))
        monkeys
    }

private fun List<String>.toMonkey(monkeys: List<Monkey>): Monkey {
    val nameLine = this.first()
    val (itemLine, operationLine, divisionLine, trueLine, falseLine) = this.drop(1) // kotlin strings dont have a component6 function :D

    val (operand, b) = operationLine.split(" = old ")[1].split(" ")

    val op = when (operand) {
        "+" -> add
        "*" -> multiply
        else -> throw RuntimeException("Fancy Maths detected")
    }

    return Monkey(
        nameLine.split(" ")[1].dropLast(1).toInt(),
        { old -> op(old, parseArg(b, old)) },
        divisionLine.split(" by ")[1].toInt(),
        { monkeys[trueLine.split(" ").last().toInt()] },
        { monkeys[falseLine.split(" ").last().toInt()] },
        itemLine.split(": ")[1].split(", ").map(String::toLong)
    )
}

private fun parseArg(a: String, old: Long) = if (a == "old") old else a.toLong()

typealias Operation = (Long) -> Long

val add = { a: Long, b: Long -> a + b }
val multiply = { a: Long, b: Long -> a * b }
