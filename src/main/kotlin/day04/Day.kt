package day04

import java.util.Scanner
import lines

class Day(val input: Scanner) {
    fun starOne(): Int = input.lines()
        .sumOf { line ->
            (winsIn(line) - 1).takeIf { it >= 0 }
                ?.let { 1 shl it } ?: 0
        }
        .also(::println)

    fun starTwo(): Int = input.lines()
        .mapIndexed { idx, line -> Card(idx, winsIn(line)) }
        .toList().run {
            sumOf { (base, wins, amount) ->
                repeat(wins) {
                    this[1 + it + base].amount += amount
                }
                1 + wins * amount
            }
        }
        .also(::println)
}

private fun String.toNumbers() =
    Regex("""\d+""").findAll(this).map { it.value }.toSet()

private fun winsIn(line: String): Int =
    line.split(":").last().split("|")
        .let { (w, c) -> w.toNumbers() intersect c.toNumbers() }
        .size

private data class Card(val index: Int, val wins: Int, var amount: Int = 1)
