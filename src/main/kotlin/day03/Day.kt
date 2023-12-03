package day03

import java.util.Scanner
import lines

private val symbolRegex = Regex(""".*[^.\d]+.*""")
private val gearRegex = Regex(""".*(\*).*""")
private val numberRegex = Regex("""(\d+)""")

private data class LocatedNumber(
    val row: Int,
    val range: IntRange,
    val value: Int
)

class Day(val input: Scanner) {
    private val grid = input.lines().toList()
    private val numbers = grid.indices.flatMap { index ->
        gridSearch(numberRegex, index)
            .map { LocatedNumber(index, it.range, it.value.toInt()) }
    }

    // ------------------------------------------------------------------------------------------------
    // Solutions
    // ------------------------------------------------------------------------------------------------

    fun starOne() = numbers
        .filterNot { num ->
            num.row.yBounds
                .map { gridSlice(it, num.range) }
                .none { symbolRegex.matches(it) }
        }
        .sumOf { it.value }
        .also(::println)

    fun starTwo() = numbers
        .fold(mutableMapOf<Pair<Int, Int>, MutableList<Int>>()) { gears, num ->
            gears.apply {
                for ((x, y) in num.findNearbyGears())
                    getOrPut(x to y) { mutableListOf() }.add(num.value)
            }
        }
        .values.filter { it.size == 2 }
        .sumOf { it.reduce(Int::times) }
        .also(::println)

    // ------------------------------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------------------------------

    private fun LocatedNumber.findNearbyGears() =
        row.yBounds.flatMap { y ->
            gridSearch(gearRegex, y, range)
                .map { range.xBounds.first + it.range.first to y }
        } // list of (x, y) of gears nearby

    private val Int.yBounds // select rows
        get() = setOf(this, minOf(this + 1, grid.lastIndex), maxOf(this - 1, 0))
    private val IntRange.xBounds // select indices
        get() = maxOf(first - 1, 0)..minOf(last + 1, grid.lastIndex)

    private fun gridSlice(row: Int, range: IntRange) = grid[row].substring(range.xBounds)
    private fun gridSearch(regex: Regex, row: Int, range: IntRange = 0..grid[row].lastIndex) =
        regex.findAll(gridSlice(row, range)).mapNotNull { it.groups.last() }
}
