package day04

import java.util.Scanner
import lines

class Day(val input: Scanner) {
    fun starOne(): Int = input.lines().map(::winsIn)
        .filter { it > 0 }.sumOf { 1 shl (it - 1) }
        .also(::println)

    fun starTwo(): Int = input.lines().map(::winsIn)
        .toList().run {
            foldIndexed(Array(size) { 1 }) { idx, acc, wins ->
                acc.apply {
                    repeat(wins) { acc[1 + it + idx] += acc[idx] }
                }
            }.sum()
        }
        .also(::println)
}

private fun String.toNumbers() =
    Regex("""\d+""").findAll(this).map { it.value }.toSet()

private fun winsIn(line: String): Int =
    line.split(":").last().split("|")
        .let { (w, c) -> w.toNumbers() intersect c.toNumbers() }
        .size
