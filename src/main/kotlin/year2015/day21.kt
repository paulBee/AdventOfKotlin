package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2
import kotlin.math.ceil

fun main() {

    val players = weapons.flatMap { weapon ->
        (armory + null).flatMap { armor ->
            (rings + null).flatMap { ring1 ->
                (rings - ring1 + null).map { ring2 ->
                    Player(weapon, armor, ring1, ring2)
                }
            }
        }
    }

    players
        .sortedBy { it.kitCost }
        .first { it.wouldBeat(Monster(104, 8, 1)) }
        .also { displayPart1(it.kitCost) }

    players
        .sortedByDescending { it.kitCost }
        .first { !it.wouldBeat(Monster(104, 8, 1)) }
        .also { displayPart2(it.kitCost) }
}

class Player(weapon: Weapon, armor: Armor?, ring1: Ring?, ring2: Ring?) {
    val hp = 100
    val damage = weapon.damage + (ring1?.damage ?: 0) + (ring2?.damage ?: 0)
    val armor = (armor?.armor?: 0) + (ring1?.armor ?: 0) + (ring2?.armor ?: 0)

    val kitCost = listOf(weapon, armor, ring1, ring2).sumBy { it?.cost ?: 0 }

    fun wouldBeat(monster: Monster): Boolean {
        val killsMonsterIn = ceil(monster.hp.toDouble() / maxOf(damage - monster.armor, 1))
        val killedIn = ceil(hp.toDouble() / maxOf(monster.damage - armor, 1))
        return killsMonsterIn <= killedIn
    }
}

val weapons = listOf(
    Weapon("Dagger", 8, 4),
    Weapon("Shortsword",10, 5),
    Weapon("Warhammer",25, 6),
    Weapon("Longsword",40, 7),
    Weapon("Greataxe",74, 8),
)

val armory =  listOf(
    Armor("Leather", 13, 1),
    Armor("Chainmail", 31, 2),
    Armor("Splintmail", 53, 3),
    Armor("Bandedmail", 75, 4),
    Armor("Platemail", 102, 5),
)

val rings = listOf(
    Ring("Damage1", 25, 1, 0),
    Ring("Damage2", 50, 2, 0),
    Ring("Damage3", 100, 3, 0),
    Ring("Defense1", 20, 0, 1),
    Ring("Defense2", 40, 0, 2),
    Ring("Defense3", 80, 0, 3),
)

open class Item(val cost: Int, val name: String)
class Weapon(name: String, cost: Int, val damage: Int): Item(cost, name)
class Armor(name: String, cost: Int, val armor: Int): Item(cost, name)
class Ring(name: String, cost: Int, val damage: Int, val armor: Int): Item(cost, name)

class Monster (val hp: Int, val damage: Int, val armor: Int)