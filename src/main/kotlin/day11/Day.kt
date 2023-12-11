package day11

import X
import Y
import allInColumn
import allInRow
import get
import asList
import collectPairs
import gridIndices
import x
import y
import java.util.Scanner
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day(val input: Scanner) {
    fun starOne() = measureDistances(factor = 1L)
    fun starTwo() = measureDistances(factor = 999_999L)

    private fun measureDistances(factor: Long): Long {
        val grid = input.asList().map { it.toList() }

        val cols = (0..grid.X).filter { grid.allInColumn(it, '.'::equals) }
        val rows = (0..grid.Y).filter { grid.allInRow(it, '.'::equals) }

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
