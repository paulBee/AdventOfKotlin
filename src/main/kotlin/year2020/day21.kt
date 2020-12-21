package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile

fun main() {
    val products = readLinesFromFile("2020/day21.txt").map { it.toProduct() }
    val ingredients = products.flatMap { it.ingredients() }.toSet()
    val allergens = products.flatMap { it.allergens() }.toSet()

    val allergenToPossibleIngredients = allergens
        .map { allergen ->
            allergen to
            products
                .filter { it.allergens().contains(allergen) }
                .map { it.ingredients() }
                .reduce { a, b -> a intersect b }
    }

    val ingredientsThatArentAllergens = ingredients - allergenToPossibleIngredients.allIngredients()

    products
        .sumBy { product -> product.ingredients().count { ingredientsThatArentAllergens.contains(it) } }
        .also(displayPart1)

    identifyAllergens(allergenToPossibleIngredients.map { it.allergen() to it.possibleIngredients() - ingredientsThatArentAllergens })
        .sortedBy { it.first }
        .joinToString(",") { it.second }
        .also(displayPart2)

}

fun identifyAllergens(unidentifiedAllergens: List<AllergenPossibilities>): List<Pair<String, String>> =
    when (unidentifiedAllergens.size) {
        0 -> emptyList()
        else -> unidentifiedAllergens.partition { it.possibleIngredients().size == 1 }
            .let { (onePossible, manyPossible) ->
                onePossible.map { it.allergen() to it.possibleIngredients().first() } +
                identifyAllergens(manyPossible.map { it.allergen() to it.possibleIngredients() - onePossible.allIngredients() })
            }
    }

typealias IngredientToAllergen = Pair<Set<String>, Set<String>>
fun IngredientToAllergen.ingredients() = this.first
fun IngredientToAllergen.allergens() = this.second

typealias AllergenPossibilities = Pair<String, Set<String>>
fun AllergenPossibilities.allergen() = this.first
fun AllergenPossibilities.possibleIngredients() = this.second
fun List<AllergenPossibilities>.allIngredients() = this.map { it.possibleIngredients() }.reduce { acc, next -> acc union next }

private fun String.toProduct(): Pair<Set<String>, Set<String>> =
    this.split(" (contains ").let { (ingredients, allergens) ->
        Pair(
            ingredients.split(" ").toSet(),
            allergens.split(")")[0].split(", ").toSet()
        )
    }

