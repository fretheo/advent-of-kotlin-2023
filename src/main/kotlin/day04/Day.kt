package day04

import ext.asList
import java.util.Scanner

class Day(val input: Scanner) : MutableMap<Int, Int> by mutableMapOf() {
    fun starOne(): Int = input.asList().map(::winsIn)
        .filter { it > 0 }.sumOf { 1 shl (it - 1) }

    fun starTwo(): Int = input.asList().map(::winsIn)
        .run { indices.sumOf { cards(it) } }

    private fun List<Int>.cards(index: Int): Int = getOrPut(index) {
        1 + (0..<index).sumOf { if (this[it] < (index - it)) 0 else cards(it) }
    }
}

private fun String.toNumbers() =
    Regex("""\d+""").findAll(this).map { it.value }.toSet()

private fun winsIn(line: String): Int =
    line.split(":").last().split("|")
        .let { it[0].toNumbers() intersect it[1].toNumbers() }.size
