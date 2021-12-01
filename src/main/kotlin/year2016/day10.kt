package year2016

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile
import kotlin.RuntimeException

fun main() {

    readLinesFromFile("2016/day10.txt").forEach { line ->
        when {
            line.startsWith("value ") -> handleValue(line)
            line.startsWith("bot ") -> handleInstr(line)
            else -> throw RuntimeException("unknown line: ${line}")
        }
    }

    displayPart2(BotsAndOutputs.getOutput("0") * BotsAndOutputs.getOutput("1") * BotsAndOutputs.getOutput("2"))
}

val valueRegex = Regex("value ([0-9]+) goes to bot ([0-9]+)")
fun handleValue(line: String) {
    val (chip, bot) = valueRegex.matchEntire(line)?.destructured ?: throw RuntimeException("Invalid value line: ${line}")
    BotsAndOutputs.getBot(bot).giveChip(chip.toInt())
}

val instrRegex = Regex("bot ([0-9]+) gives low to (bot|output) ([0-9]+) and high to (bot|output) ([0-9]+)")
fun handleInstr(line: String) {
    val (bot, lowType, lowName, highType, highName) = instrRegex.matchEntire(line)?.destructured ?: throw RuntimeException("Invalid instr line: ${line}")

    val low = if (lowType == "bot") BotRecipient(lowName) else OutputRecipient(lowName)
    val high = if (highType == "bot") BotRecipient(highName) else OutputRecipient(highName)

    BotsAndOutputs.getBot(bot).giveInstruction(Instr(low, high))
}

sealed class Recipient(val name: String)
class BotRecipient(name: String): Recipient(name)
class OutputRecipient(name: String): Recipient(name)

data class Instr(val low: Recipient, val high: Recipient)

object BotsAndOutputs {
    private val builtBots = mutableMapOf<String, Bot>()
    private val outputs = mutableMapOf<String, Int>()

    fun getOutput(name : String) = outputs[name]!!

    fun getBot(name: String): Bot = builtBots.getOrPut(name) { Bot(name, this::getBot, outputs::put) }
}

class Bot(val name: String, val getBot: (String) -> Bot, val setOuput: (String, Int) -> Unit) {
    private var chip1 : Int? = null
    private var chip2 : Int? = null
    private var instruction: Instr? = null

    fun giveChip(chip: Int) {
        if (chip1 == null) {
            chip1 = chip
        } else {
            chip2 = chip
        }
        actIfReady()
    }

    fun giveInstruction(instr: Instr) {
        instruction = instr
        actIfReady()
    }

    private fun actIfReady() {
        val chip1 = chip1
        val chip2 = chip2
        val instr = instruction
        if (chip1 != null && chip2 != null && instr != null) {
            val low = if (chip1 < chip2) chip1 else chip2
            val high = if (chip1 < chip2) chip2 else chip1

            if (low == 17 && high == 61) {
                displayPart1(name)
            }

            send(low, instr.low)
            send(high, instr.high)
        }
    }

    private fun send(value: Int, recipient: Recipient) = when(recipient) {
        is BotRecipient -> getBot(recipient.name).giveChip(value)
        is OutputRecipient -> setOuput(recipient.name, value)
    }
}
