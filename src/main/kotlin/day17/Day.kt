package day17

import ext.Direction.*
import ext.*
import java.util.PriorityQueue
import java.util.Scanner

class Day(val input: Scanner) {
    fun starOne() = input.asGridOf(Char::digitToInt).run { HeatCalculator(this, 1..3) }.calculate()
    fun starTwo() = input.asGridOf(Char::digitToInt).run { HeatCalculator(this, 4..10) }.calculate()
}

private class HeatCalculator(val grid: Grid<Int>, val steps: IntRange) {
    private val queue = PriorityQueue<Record> { a, b -> a.heat - b.heat }
    private val check = mutableSetOf<Triple<Int, Int, Direction>>()

    fun calculate(): Int {
        with(grid) {
            findTurnPoints(0, 0, L).let(queue::addAll)
            findTurnPoints(0, 0, D).let(queue::addAll)

            while (true) queue.poll().also { (x, y, heat, direction) ->
                if (x to y == X to Y) return heat

                if (!check.add(Triple(x, y, direction))) return@also

                findTurnPoints(x, y, direction)
                    .map { it.copy(heat = heat + it.heat) }
                    .let { queue.addAll(it) }
            }
        }
    }

    private fun Grid<Int>.findTurnPoints(x: Int, y: Int, baseDirection: Direction) =
        perpendicularTo(baseDirection).flatMap { direction ->
            listTurnPoints(x, y, steps.last, direction)
                // associate with original heat values
                .associateWith { get(it.x, it.y) }.toList()
                // accumulate heat per step
                .runningReduce { (_, acc), (point, heat) -> (point.x to point.y) to acc + heat }
                // exclude steps that are not allowed
                .drop(steps.first - 1)
                .map { Record(it.first.x, it.first.y, it.second, direction) }
        }

    private fun Grid<Int>.listTurnPoints(x: Int, y: Int, distance: Int, direction: Direction) = (1..distance)
        .map { s -> (x to y).at(s, direction) }
        .filter { it.x in xIndices && it.y in yIndices }

    private fun Point.at(distance: Int, direction: Direction) = when (direction) {
        U -> x to y - distance
        L -> x - distance to y
        D -> x to y + distance
        R -> x + distance to y
    }

    private fun perpendicularTo(direction: Direction) = when (direction) {
        R, L -> listOf(U, D)
        U, D -> listOf(R, L)
    }

    private data class Record(val x: Int, val y: Int, val heat: Int, val direction: Direction)
}
