package year2016

import utils.aoc.displayPart1

//F4  .   .   .   .   .   .   .   .   .   .   .
//F3  .   .   .   .   .   .   .   PrG PrM RG  RM
//F2  .   .   .   .   PM  .   SM  .   .   .   .
//F1  E   TG  TM  PG  .   SG  .   .   .   .   .



fun main() {

    val initState = State(0, 0, listOf(
        Floor(setOf("T", "P", "S"), setOf("T")),
        Floor(setOf(), setOf("P", "S")),
        Floor(setOf("Pr", "R"), setOf("Pr", "R")),
        Floor(setOf(), setOf()),
    ))

    displayPart1(findFinalState(setOf(initState), setOf(initState)).step)

}

tailrec fun findFinalState(latestStates: Set<State>, allStates: Set<State>): State =
    when {
        latestStates.any { it.isFinished() } -> latestStates.first { it.isFinished() }
        else -> {
            println(latestStates.size)
            val newStates = latestStates.flatMap { next(it) }
                .filter { new -> allStates.none { old -> old.isSame(new) } }
                .toSet()

            findFinalState(newStates, allStates + newStates)
        }
    }

fun next(state: State): Set<State> {
    val transitions = state.currentFloor.combinations()

    val elevatorPositions = listOf(state.elevatorPos - 1, state.elevatorPos + 1).filter { it in 0..3 }

    return elevatorPositions.flatMap { newFloor ->
        transitions.map { (gen, micro) ->
            State(
                state.step + 1,
                newFloor,
                state.floors.mapIndexed { i, floor -> when(i) {
                    newFloor -> Floor(floor.generators + gen, floor.microchips + micro)
                    state.elevatorPos -> Floor(floor.generators - gen, floor.microchips - micro)
                    else -> floor
                }

                }
            )
        }
    }
        .filter { it.isValid() }
        .fold(emptySet()) { acc, next ->
            if (acc.any { it.isSame(next) }) acc else acc + next
        }

}

data class State(val step: Int, val elevatorPos: Int, val floors: List<Floor>) {
    fun isValid() = floors.all { it.isValid() }

    fun isSame(other: State) =
        genToChip(this) == genToChip(other)
                && elevatorPos == other.elevatorPos

    fun isFinished() = floors.dropLast(1).all { it.isEmpty() }

    val currentFloor by lazy { floors[elevatorPos] }
}

fun genToChip(state: State): Set<Pair<Int, Int>> =
    listOf("T", "P", "S", "Pr", "R")
        .map { letter ->
            state.floors.indexOfFirst { it.generators.contains(letter) } to
                    state.floors.indexOfFirst { it.microchips.contains(letter)
                    }
        }
        .toSet()

data class Floor(val generators: Set<String>, val microchips: Set<String>) {

    fun isValid() = microchips.isEmpty()
            || microchips.all { generators.contains(it) }
            || generators.isEmpty()

    fun isEmpty() = generators.isEmpty() && microchips.isEmpty()

    fun combinations() = setOf(
        generators.map { setOf(it) to emptySet<String>() },
        microchips.map { emptySet<String>() to setOf(it) },
        generators.flatMap { g -> microchips.map { m -> setOf(g) to setOf(m) } },
        generators.flatMap { first -> generators.filter { it != first }.map { setOf(first, it) to emptySet<String>() } },
        microchips.flatMap { first -> microchips.filter { it != first }.map { emptySet<String>() to setOf(first, it)  } }
    ).flatMap { it }.toSet()

}