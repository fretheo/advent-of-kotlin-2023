package day14

import ext.*
import java.util.Scanner

const val N = 1_000_000_000

class Day(val input: Scanner) {
    fun starOne() = input.lines().asMutableGrid()
        .tiltNorth()
        .let(::loadOf)

    fun starTwo(): Int {
        var grid = input.lines().asMutableGrid()
        val gridLoads = mutableListOf<Pair<MutableGrid<Char>, Int>>()

        repeat(N) {
            grid = tiltCycle(grid)

            val gridIndex = gridLoads.map { it.first }.indexOf(grid)

            if (gridIndex < 0) {
                gridLoads += grid to loadOf(grid)
                return@repeat
            }

            return gridLoads.drop(gridIndex)
                .let { cycle -> cycle[(N - it) % cycle.size] }
                .second
        }

        return loadOf(grid)
    }
}

// ------------------------------------------------------------------------------------------------
// Helpers - Counter
// ------------------------------------------------------------------------------------------------

private fun loadOf(grid: List<MutableList<Char>>) =
    grid.foldColRow(0) { acc, _, y, c -> acc + if (c == 'O') grid.size - y else 0 }

// ------------------------------------------------------------------------------------------------
// Helpers - Processing
// ------------------------------------------------------------------------------------------------

private fun MutableGrid<Char>.tilt() = onEach { row ->
    var skip = 0
    while (skip < row.size) {
        val sub = row.drop(skip)

        // index of empty space
        val E = sub.indexOfFirst { it in ".#" }
        if (E < 0) break
        if (sub[E] == '#') {
            skip += E + 1
            continue
        }

        // index of first stone
        val S = sub.drop(E + 1).indexOfFirst { it in "O#" }
        if (S < 0) break
        if (sub[S + E + 1] == '#') {
            skip += E + 1 + S
            continue
        }

        // move the stone
        row[E + skip] = 'O'
        row[E + 1 + S + skip] = '.'
    }
}

// ------------------------------------------------------------------------------------------------
// Helpers - Misc
// ------------------------------------------------------------------------------------------------

private fun tiltCycle(grid: List<MutableList<Char>>) = grid
    .tiltNorth().tiltWest().tiltSouth().tiltEast()

private fun MutableGrid<Char>.tiltSouth() = this
    .transposed().map { it.asReversed().toMutableList() }
    .tilt()
    .transposed().asReversed().map { it.toMutableList() }

private fun MutableGrid<Char>.tiltNorth() = this
    .transposed().map { it.toMutableList() }
    .tilt()
    .transposed().map { it.toMutableList() }

private fun MutableGrid<Char>.tiltEast() = this
    .map { it.asReversed() }
    .tilt()
    .map { it.asReversed() }

private fun MutableGrid<Char>.tiltWest() = tilt()
