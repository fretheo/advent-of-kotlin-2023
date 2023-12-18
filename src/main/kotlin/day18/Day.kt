package day18

import ext.LongPoint as Point
import ext.Direction.*
import ext.*
import java.lang.Integer.parseInt
import java.util.Scanner
import kotlin.math.roundToLong

class Day(val input: Scanner) {
    fun starOne() = parseBasic().run(::process)
    fun starTwo() = parseColor().run(::process)

    private fun process(commands: Sequence<Pair<Direction, Int>>) = commands
        .runningFold((0L to 0L) to 0L) { (prev, _), (direction, length) ->
            prev.next(direction, length) to length.toLong()
        }
        .windowed(2).map { (prev, next) ->
            val a = prev.first
            val (b, distance) = next
            (a.x * b.y - a.y * b.x + distance) / 2.0
        }.sum().roundToLong() + 1

    private fun Point.next(direction: Direction, distance: Int) = when (direction) {
        U -> x to y - distance
        D -> x to y + distance
        L -> x - distance to y
        R -> x + distance to y
    }

    private fun parseBasic() = input.lines()
        .map { it.split(" ") }
        .map { (dir, dist) -> Direction.valueOf(dir) to dist.toInt() }

    private fun parseColor() = input.lines()
        .map { Regex(""".*\(#(.*)\)""").find(it)!!.groupValues.last() }
        .map { it.take(5) to it.last() }
        .map { (distance, code) ->
            arrayOf(R, D, L, U)[code.digitToInt()] to parseInt(distance, 16)
        }
}
