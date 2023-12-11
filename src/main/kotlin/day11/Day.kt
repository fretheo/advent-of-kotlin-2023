package day11

import ext.*
import java.util.Scanner
import kotlin.math.*

class Day(val input: Scanner) {
    fun starOne() = measureDistances(factor = 1L)
    fun starTwo() = measureDistances(factor = 999_999L)

    private fun measureDistances(factor: Long): Long {
        val grid = input.asList().map { it.toList() }

        val cols = grid.xIndices.filter { grid.allInCol(it, '.'::equals) }
        val rows = grid.yIndices.filter { grid.allInRow(it, '.'::equals) }

        return grid.gridIndices
            .filter { (x, y) -> grid[x, y] == '#' }
            .collectPairs { it.first != it.second }
            .sumOf { (a, b) ->
                val xCount = cols.count { it in min(a.x, b.x)..max(a.x, b.x) }
                val yCount = rows.count { it in min(a.y, b.y)..max(a.y, b.y) }

                abs(b.y - a.y) + abs(b.x - a.x) + (xCount + yCount) * factor
            }
    }
}
