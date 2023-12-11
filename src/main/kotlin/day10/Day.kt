package day10

import day10.Direction.*
import java.util.Scanner
import ext.*

class Day(val input: Scanner) {
    fun starOne(): Int {
        val (grid, point, direction) = parseInputs(input)

        var counter = 0
        PipeVisitor(grid).traverse(point, direction) { _, _ -> counter++ }
        return counter / 2
    }

    fun starTwo(): Int {
        val loop = parseInputs(input)
            .let { (grid, point, direction) ->
                PipeVisitor(grid)
                    .apply { traverse(point, direction) }
                    .visited.fold(grid.replaceWith(' ')) { acc, (x, y) ->
                        acc.apply { set(x, y, grid[x, y]) }
                    }
            }

        val y = loop.indexOfFirst { !it.all(' '::equals) }
        val x = loop[y].indexOfFirst { it != ' ' }
        var prevDirection = D
        PipeVisitor(loop).traverse(x to y, prevDirection) { point, direction ->
            loop.clearAround(point, prevDirection)
            if (prevDirection != direction)
                loop.clearAround(point, direction)

            prevDirection = direction
        }

        return loop.sumOf { it.count('X'::equals) }
    }

    private fun List<MutableList<Char>>.clearAround(point: Pair<Int, Int>, direction: Direction) {
        var (x, y) = point
        when (direction) {
            D -> while (x < X && this[y][x + 1] in " X") this[y][++x] = 'X'
            U -> while (x > 0 && this[y][x - 1] in " X") this[y][--x] = 'X'
            L -> while (y < Y && this[y + 1][x] in " X") this[++y][x] = 'X'
            R -> while (y > 0 && this[y - 1][x] in " X") this[--y][x] = 'X'
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Helpers - Parsing
// ------------------------------------------------------------------------------------------------

private fun parseInputs(input: Scanner): Triple<List<MutableList<Char>>, Pair<Int, Int>, Direction> {
    val grid = input.asList().map { it.toMutableList() }

    val (x, y) = grid.positionOfFirst { it == 'S' }
    grid[x, y] = grid.decodeSymbolAt(x, y)

    val direction = when (grid[x, y]) {
        in "7F|" -> D
        in "LJ"  -> U
        else     -> L
    }

    return Triple(grid, x to y, direction)
}

private fun List<List<Char>>.decodeSymbolAt(x: Int, y: Int): Char {
    var candidates = "LF7J-|".toSet()
    if (x < X && this[y][x + 1] in "7J-") candidates = candidates intersect "LF-".toSet()
    if (x > 0 && this[y][x - 1] in "LF-") candidates = candidates intersect "7J-".toSet()
    if (y < Y && this[y + 1][x] in "LJ|") candidates = candidates intersect "7F|".toSet()
    if (y > 0 && this[y - 1][x] in "7F|") candidates = candidates intersect "LJ|".toSet()
    return candidates.single()
}

// ------------------------------------------------------------------------------------------------
// Helpers - Traversing
// ------------------------------------------------------------------------------------------------

private class PipeVisitor<T : List<List<Char>>>(private val grid: T) {
    private val _visited = mutableSetOf<Point>()
    val visited get() = _visited.asSequence()

    private fun go(x: Int, y: Int, direction: Direction) = when (direction) {
        U -> (x to y - 1) to when (grid[x, y - 1]) {
            '7'  -> L
            'F'  -> R
            else -> direction
        }

        D -> (x to y + 1) to when (grid[x, y + 1]) {
            'J'  -> L
            'L'  -> R
            else -> direction
        }

        L -> (x - 1 to y) to when (grid[x - 1, y]) {
            'L'  -> U
            'F'  -> D
            else -> direction
        }

        R -> (x + 1 to y) to when (grid[x + 1, y]) {
            'J'  -> U
            '7'  -> D
            else -> direction
        }
    }

    tailrec fun traverse(point: Point, direction: Direction, onVisit: (Point, Direction) -> Unit = { _, _ -> }) {
        if (point in _visited) return

        onVisit.invoke(point, direction)

        val (x, y) = point.also(_visited::add)
        val (nextPoint, nextOrientation) = go(x, y, direction) ?: return

        traverse(nextPoint, nextOrientation, onVisit)
    }
}

// ------------------------------------------------------------------------------------------------
// Helpers - Misc: types and extensions
// ------------------------------------------------------------------------------------------------

private enum class Direction { U, D, L, R }
