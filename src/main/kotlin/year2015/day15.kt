package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2

fun main() {

    maximizeRecipe().also { displayPart1(it.score) }

    generateSequence(0) { it + 1 }
        .map { maximizeRecipe(it) }
        .flatMap { listOf(
            Recipe(it.frosting - 1, it.candy + 1, it.butterscotch, it.sugar),
            Recipe(it.frosting - 1, it.candy, it.butterscotch + 1, it.sugar),
            Recipe(it.frosting + 1, it.candy - 1, it.butterscotch, it.sugar),
            Recipe(it.frosting, it.candy - 1, it.butterscotch + 1, it.sugar),
            Recipe(it.frosting + 1, it.candy, it.butterscotch - 1, it.sugar),
            Recipe(it.frosting, it.candy + 1, it.butterscotch - 1, it.sugar)
        ) }
        .first { it.calories == 500 }.also { displayPart2(it.score) }


}

private fun maximizeRecipe(initialSugar: Int = 1) = generateSequence(Recipe(0, 0, 0, initialSugar))
{
    listOf(
        Recipe(it.frosting + 1, it.candy, it.butterscotch, it.sugar),
        Recipe(it.frosting, it.candy + 1, it.butterscotch, it.sugar),
        Recipe(it.frosting, it.candy, it.butterscotch + 1, it.sugar),
        Recipe(it.frosting, it.candy, it.butterscotch, it.sugar + 1),
    )
        .sortedWith(compareBy({ it.score }, { it.mostNegative }))
        .last()
}
    .takeWhile { it.size <= 100 }
    .last()

private class Recipe (val frosting: Int, val candy: Int, val butterscotch: Int, val sugar: Int) {

    val size = frosting + candy + butterscotch + sugar

    val capacity = (4 * frosting) - butterscotch
    val durability = (5 * candy) - (2 * frosting)
    val flavour = (5 * butterscotch) - (2 * sugar) - candy
    val texture = 2 * sugar

    val mostNegative = listOf(capacity, durability, flavour, texture).minOrNull()!!

    val calories = (5 * frosting) + (8 * candy) + (6 * butterscotch) + sugar

    val score = capacity.let { maxOf(it, 0) } * durability.let { maxOf(it, 0) } * flavour.let { maxOf(it, 0) } * texture.let { maxOf(it, 0) }

}