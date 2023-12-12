package day02

import java.util.Scanner
import ext.lines

class Day(val input: Scanner) {
    fun starOne(): Int = input.lines()
        .filter { line -> line.maxByColors().mapIndexed { idx, it -> 12 + idx - it }.all { it >= 0 } }
        .sumOf { line -> line.drop(5).takeWhile { it != ':' }.toInt() }

    fun starTwo(): Int = input.lines()
        .sumOf { it.maxByColors().reduce(Int::times) }
}

private val colors = listOf("red", "green", "blue")
private fun String.maxByColors() = colors.map { color ->
    Regex("""(\d+) $color""").findAll(this)
        .maxOfOrNull { it.groupValues.last().toInt() } ?: 0
}
