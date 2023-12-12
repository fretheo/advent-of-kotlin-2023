package day02

import java.util.Scanner
import ext.lines

private val colors = listOf("red", "green", "blue")

class Day(val input: Scanner) {
    fun starOne(): Int = input.lines()
        .sumOf { line ->
            colors.withIndex()
                .all { line.maxByColor(it.value) <= arrayOf(12, 13, 14)[it.index] }
                .let { valid -> if (valid) id(line) else 0 }
        }

    fun starTwo(): Int = input.lines()
        .sumOf { line ->
            colors
                .map(line::maxByColor)
                .reduce(Int::times)
        }
}

private fun id(line: String) = line.drop(5).takeWhile { it != ':' }.toInt()
private fun String.maxByColor(color: String) = Regex("""(\d+) $color""")
    .findAll(this)
    .map { it.groupValues.last().toInt() }
    .maxOrNull() ?: 0
