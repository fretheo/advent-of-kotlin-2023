package day03

import java.util.Scanner
import ext.lines

private data class LocatedNumber(
    val row: Int,
    val range: IntRange,
    val value: Int
)

class Day(val input: Scanner) {
    private val grid = input.lines().toList()
    private val numbers = grid.indices.flatMap { index ->
        gridSearch(Regex("""(\d+)"""), index)
            .map { LocatedNumber(index, it.range, it.value.toInt()) }
    }
    private val symbolPoints = grid.indices.flatMap { index ->
        gridSearch(Regex("""[^.\d]"""), index).map { it.range.first to index }
    }
    private val gearPoints = grid.indices.flatMap { index ->
        gridSearch(Regex("""\*"""), index).map { it.range.first to index }
    }

    // ------------------------------------------------------------------------------------------------
    // Solutions
    // ------------------------------------------------------------------------------------------------

    fun starOne() = numbers
        .filter {
            symbolPoints.any { point -> point.isNear(it) }
        }
        .sumOf(LocatedNumber::value)
        .also(::println)

    fun starTwo() = gearPoints
        .map {
            numbers.filter { num -> it.isNear(num) }.map { it.value }
        }
        .filter { it.size == 2 }
        .sumOf { it.reduce(Int::times) }
        .also(::println)

    // ------------------------------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------------------------------

    private val Int.yBounds // select rows
        get() = setOf(this, minOf(this + 1, grid.lastIndex), maxOf(this - 1, 0))
    private val IntRange.xBounds // select indices
        get() = maxOf(first - 1, 0)..minOf(last + 1, grid.lastIndex)

    private fun Pair<Int, Int>.isNear(number: LocatedNumber) =
        first in number.range.xBounds && second in number.row.yBounds

    private fun gridSearch(regex: Regex, row: Int, range: IntRange = 0..grid[row].lastIndex) =
        regex.findAll(grid[row].substring(range.xBounds)).mapNotNull { it.groups.last() }
}
