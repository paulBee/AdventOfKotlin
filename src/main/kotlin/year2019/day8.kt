package year2019

import utils.aoc.readTextFromFile
import java.lang.IllegalStateException

fun main() {
    val encrpytedInput = readTextFromFile("2019/day8.txt").map { it.toString().toInt() }

    val layerSize = 25*6

    val layers = encrpytedInput.chunked(layerSize)
    val answer = layers
        .minByOrNull { layer -> layer.count { it == 0 } }
        ?.let { layer -> layer.count { it == 1 } * layer.count { it == 2 } }
    println(answer)

    val pixels = (0 until layers[0].size)
        .map { index -> layers.map { it[index] } }
        .map { blackOrWhite(it) }

    pixels.chunked(25)
        .forEach { println(it.joinToString("")) }

}

fun blackOrWhite(pixelLayers: List<Int>) =
    pixelLayers.first { it != 2 }
        .let {
            when (it) {
                0 -> " "
                1 -> "X"
                else -> throw IllegalStateException("Crazy pixel")
            }
        }

