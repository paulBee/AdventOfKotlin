package year2019

import utils.aoc.readLinesFromFile
import kotlin.RuntimeException
import kotlin.math.ceil

const val fuel = "FUEL"
const val ore = "ORE"
const val max_ore = 1000000000000L

fun main() {
    val reactions = readLinesFromFile("2019/day14.txt").map { it.toReaction() }

    val chemicalToComplexity = distanceFromORE(reactions).fold(HashMap<Chemical, Long>()) {
        map, pair ->
        map[pair.first] = pair.second
        map
    }

    val oreFor1Fuel = oreForFuel(chemicalToComplexity, reactions, 1)

    println("$oreFor1Fuel ore is needed for 1 fuel")

    // simple multiplication of the ore:fuel ratio is always an underestimate due to the waste being ignored
    // so just keep performing that simple alteration until you overshoot
    // (need to try to overshoot by adding 1 when fuelInc is zero incase there is enough waste for one more
    // ...I think at least, none of the examples needed this)
    val (oreNeeded, fuelGenerated) = generateSequence(Pair(oreFor1Fuel, 1L)) {
        val fuelInc = (max_ore - it.first) / oreFor1Fuel
        val fuelGenerated = it.second + if (fuelInc == 0L) 1L else fuelInc
        val oreNeeded = oreForFuel(chemicalToComplexity, reactions, fuelGenerated)
        Pair(oreNeeded, fuelGenerated)
    }
        .takeWhile { max_ore > it.first }
        .last()

    println("$oreNeeded ore is needed for $fuelGenerated fuel")

}

private fun oreForFuel(
    chemicalToComplexity: HashMap<Chemical, Long>,
    reactions: List<Reaction>,
    amount: Long
): Long {
    var requirements = hashMapOf(Pair(fuel, amount))
    while (requirements.keys.any { it != ore }) {
        requirements = simplify(requirements, chemicalToComplexity, reactions)
    }
    return requirements[ore]?: throw RuntimeException("I hate nulls")
}

fun simplify(
    requirements: HashMap<String, Long>,
    chemicalToComplexity: HashMap<Chemical, Long>,
    reactions: List<Reaction>
): HashMap<String, Long> {
    val nextToSimplify = requirements.keys.maxByOrNull { chemicalToComplexity[it] ?: throw RuntimeException("wtf") } ?: throw RuntimeException("wtf")
    val amountNeeded = requirements[nextToSimplify] ?: throw RuntimeException("wtf")
    requirements.remove(nextToSimplify)

    val newIngredients = ingredients(AmountOfChemical(amountNeeded, nextToSimplify), reactions)
    newIngredients.forEach {
        requirements[it.chemical] = requirements.getOrDefault(it.chemical, 0L) + it.amount
    }

    return requirements
}

fun ingredients(toMake: AmountOfChemical, reactions: List<Reaction>): List<AmountOfChemical> {
    val reaction = reactions
        .find { it.output.chemical == toMake.chemical }
        ?: throw RuntimeException("No recipe for ${toMake.chemical}")

    val numberOfReactions = ceil(toMake.amount.toDouble() / reaction.output.amount).toLong()

    return reaction.ingredients
        .map { AmountOfChemical(it.amount * numberOfReactions, it.chemical) }
}


// parsing input

fun distanceFromORE(reactions: List<Reaction>): List<Pair<String, Long>> {
    val buildable = mutableListOf(Pair(ore, 0L))

    return addRemaining(reactions, buildable, 1L)
}

private tailrec fun addRemaining(
    reactions: List<Reaction>,
    buildable: MutableList<Pair<String, Long>>,
    level: Long
): List<Pair<String, Long>> {

    if (reactions.isEmpty()) {
        return buildable
    }

    val buildableAtThisLevel = reactions.filter { reaction ->
        reaction.ingredients
            .all { ingredient -> buildable.map { it.first }.contains(ingredient.chemical) }
    }
    val stillNotBuildable = reactions.filter { reaction ->
        !reaction.ingredients
            .all { ingredient -> buildable.map { it.first }.contains(ingredient.chemical) }
    }

    buildable.addAll(buildableAtThisLevel.map { Pair(it.output.chemical, level) })

    return addRemaining(stillNotBuildable, buildable, level + 1)
}

typealias Chemical = String

data class AmountOfChemical(val amount: Long, val chemical: Chemical)

data class Reaction(val output: AmountOfChemical, val ingredients: List<AmountOfChemical>)
private fun String.toReaction(): Reaction {
    val (inputStr, outputStr) = this.split("=>").map { it.trim() }
    return Reaction(
        outputStr.toAmountOfChemical(),
        inputStr.split(",")
            .map { it.trim() }
            .map { it.toAmountOfChemical() }
        )
}

val amountRegex = """(\d+) (.*)""".toRegex()
private fun String.toAmountOfChemical(): AmountOfChemical {
    val (volume, chemical) = amountRegex.matchEntire(this)
        ?.destructured
        ?: throw RuntimeException("not a match $this")

    return AmountOfChemical(volume.toLong(), chemical)
}
