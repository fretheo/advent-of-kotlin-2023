package day13

import ext.*
import java.util.Scanner

class Day(val input: Scanner) {
    fun starOne() = parseGrids().sumOf { it.measureMirror(smudged = false) }
    fun starTwo() = parseGrids().sumOf { it.measureMirror(smudged = true) }

    private fun parseGrids(): Sequence<Grid<Char>> = sequence {
        while (input.hasNextLine()) input.lines()
            .takeWhile(String::isNotEmpty).asGrid()
            .let { yield(it) }
    }

    private fun Grid<Char>.measureMirror(smudged: Boolean): Int =
        countMirrorLanes(smudged)?.times(100)
            ?: transposed().countMirrorLanes(smudged)!!

    private fun Grid<Char>.countMirrorLanes(smudged: Boolean): Int? = selectMirrorLanes()
        .firstNotNullOfOrNull { idx ->
            val P = listIterator(idx + 1)
            val N = listIterator(idx + 1)

            var fixed = false
            var wrong = false

            while (!wrong && P.hasPrevious() && N.hasNext()) {
                val (a, b) = P.previous() to N.next()

                if (a == b) continue

                if (smudged && !fixed && a.indices.count { a[it] != b[it] } == 1)
                    fixed = true
                else
                    wrong = true
            }

            if ((!smudged || fixed) && !wrong) return idx + 1 else null
        }

    private fun Grid<Char>.selectMirrorLanes() = windowed(2)
        .withIndex().filter { iv ->
            val (a, b) = iv.value
            a == b || a.indices.count { a[it] != b[it] } == 1
        }
        .map { it.index }
}
