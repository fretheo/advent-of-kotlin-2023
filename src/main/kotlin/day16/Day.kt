package day16

import ext.Direction.*
import ext.*
import java.util.Scanner

class Day(val input: Scanner) {
    fun starOne() = input.asGrid().let(::Reflector)
        .go(0, 0, R).energizedCount()

    fun starTwo() = input.asGrid().let { G ->
        (listOf(
            Reflector(G).go(0, 0, R),
            Reflector(G).go(0, 0, D),
            Reflector(G).go(G.X, G.Y, L),
            Reflector(G).go(G.X, G.Y, U),
        ) + listOf(
            (1..<G.X).map { Reflector(G).go(it, 0, D) },
            (1..<G.X).map { Reflector(G).go(it, G.Y, U) },
            (1..<G.Y).map { Reflector(G).go(0, it, L) },
            (1..<G.Y).map { Reflector(G).go(G.X, it, R) },
        ).flatten()).maxOf(Reflector::energizedCount)
    }
}

private class Reflector(private val G: Grid<Char>) {
    private val marked = List(G.Y + 1) { MutableList(G.X + 1) { 0 } }
    private val cached = mutableSetOf<Triple<Int, Int, Direction>>()

    fun energizedCount() =
        marked.foldRowCol(0) { acc, _, _, i -> acc + i }

    fun go(x: Int, y: Int, d: Direction): Reflector = apply {
        if (x !in G.xIndices || y !in G.yIndices || Triple(x, y, d) in cached) return@apply

        cached.add(Triple(x, y, d))
        marked[y][x] = 1

        when {
            G[x, y] == '/' -> when (d) {
                R -> go(x, y - 1, U)
                L -> go(x, y + 1, D)
                U -> go(x + 1, y, R)
                D -> go(x - 1, y, L)
            }

            G[x, y] == '\\' -> when (d) {
                R -> go(x, y + 1, D)
                L -> go(x, y - 1, U)
                U -> go(x - 1, y, L)
                D -> go(x + 1, y, R)
            }

            G[x, y] == '|' && d in setOf(L, R) -> {
                go(x, y - 1, U)
                go(x, y + 1, D)
            }

            G[x, y] == '-' && d in setOf(U, D) -> {
                go(x - 1, y, L)
                go(x + 1, y, R)
            }

            d == U -> go(x, y - 1, d)
            d == D -> go(x, y + 1, d)
            d == L -> go(x - 1, y, d)
            d == R -> go(x + 1, y, d)
        }
    }
}
