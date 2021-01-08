package year2015

import utils.aoc.displayPart1
import utils.aoc.displayPart2

fun main() {
    winningMana(emptyList(), 50, 500, 71, YOURS, 0, false)?.also(displayPart1)
    winningMana(emptyList(), 50, 500,71, YOURS, 0, true)?.also(displayPart2)
}

fun winningMana(
    activeSpells: List<Spell>,
    playerHealthInit: Int,
    playerManaInit: Int,
    bossHealthInit: Int,
    turn: Turn,
    manaSpent: Int,
    hardmode: Boolean
): Int? {

    val playerHealth = playerHealthInit - if (hardmode && turn == YOURS) 1 else 0
    if (playerHealth == 0) return null

    val damage = activeSpells.fold(0) { acc, spell -> acc + spell.damage }
    val armor = activeSpells.fold(0) { acc, spell -> acc + spell.armor }
    val health = activeSpells.fold(playerHealth) { acc, spell -> acc + spell.health }
    val mana = activeSpells.fold(playerManaInit) { acc, spell -> acc + spell.mana }
    val bossHealth = bossHealthInit - damage

    val persistingSpells = activeSpells.decrement()
    return if (bossHealth <= 0) manaSpent
    else when(turn) {
        BOSSs -> when (val bossDamage = 10 - armor) {
            in health..Int.MAX_VALUE -> null
            else -> winningMana(
                persistingSpells,
                health - bossDamage,
                mana,
                bossHealth,
                YOURS,
                manaSpent,
                hardmode
            )
        }
        YOURS -> spellBook()
                .filter { possible -> persistingSpells.none { it.name == possible.name } }
                .filter { it.cost <= mana }
                .map { spell -> winningMana(
                    persistingSpells + spell,
                    health,
                    mana - spell.cost,
                    bossHealth,
                    BOSSs,
                    manaSpent + spell.cost,
                    hardmode
                ) }
                .minByOrNull { it?: Int.MAX_VALUE }
    }
}

private fun List<Spell>.decrement() = this.mapNotNull { it.tick() }

sealed class Turn
object YOURS: Turn()
object BOSSs: Turn()

fun spellBook() = setOf(
    Spell("magic missile", 53, damage = 4),
    Spell("drain", 73, damage = 2, health = 2),
    Spell("shield", 113, armor = 7, duration = 6),
    Spell("poison", 173, damage = 3, duration = 6),
    Spell("recharge", 229, mana = 101, duration = 5)
)

data class Spell (
    val name: String,
    val cost: Int,
    val armor: Int = 0,
    val damage: Int = 0,
    val health: Int = 0,
    val mana: Int = 0,
    val duration: Int = 1,
) {
    fun tick() = if (duration > 1)
        Spell(this.name, this.cost, this.armor, this.damage, this.health, this.mana, this.duration - 1)
    else
        null
}
