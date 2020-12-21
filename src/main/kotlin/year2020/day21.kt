package year2020

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import utils.aoc.readLinesFromFile

fun main() {
    val products = readLinesFromFile("2020/day21.txt").map { it.toProduct() }
    val ingredients = products.flatMap { it.ingredients }.toSet()
    val allergens = products.flatMap { it.allergens }.toSet()

    val allergenToPossibleIngredients = allergens
        .map { allergen -> allergen to products
            .filter { it.allergens.contains(allergen) }
            .map { it.ingredients }
            .reduce { a, b -> a intersect b }
    }.toMap()
    
    val ingredientsThatCouldBeAllergens = allergenToPossibleIngredients.values.reduce { acc, next -> acc union next }
    val ingredientsThatArentAllergens = ingredients - ingredientsThatCouldBeAllergens

    products.sumBy { product -> product.ingredients.count { ingredientsThatArentAllergens.contains(it) } }.also(displayPart1)

    val allergenToPossibleActiveIngredients = allergenToPossibleIngredients.mapValues { it.value - ingredientsThatArentAllergens }

    val allergenToIngredient = identifyAllergens(allergenToPossibleActiveIngredients.entries.map { it.key to it.value })

    allergenToIngredient.sortedBy { it.first }.joinToString(",") { it.second }.also(displayPart2)

}

fun identifyAllergens(allergenToPossibleActiveIngredients: List<Pair<String, Set<String>>>): List<Pair<String, String>> {

    if (allergenToPossibleActiveIngredients.size == 0) {
        return emptyList()
    }

    val (identified, unidentified) = allergenToPossibleActiveIngredients.partition { it.second.size == 1 }

    val identifiedIngredients = identified.flatMap { it.second }.toSet()

    return identified.map { (key, values) -> key to values.first() } + identifyAllergens(unidentified.map { it.first to it.second - identifiedIngredients })

}

data class Product(val ingredients: Set<String>, val allergens: Set<String>) {

    fun removeIngredients(ingredientsThatArentAllergens: Set<String>): Pair<Set<String>, Set<String>> {
        return allergens to ingredients - ingredientsThatArentAllergens
    }
}

private fun String.toProduct(): Product {
    val (ingredients, allergens) = this.split(" (contains ")

    return Product(ingredients.split(" ").toSet(), allergens.split(")")[0].split(", ").toSet())
}
