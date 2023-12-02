package day02

import java.util.Scanner
import kotlin.math.max
import lines

class Day(val input: Scanner) {
    fun starOne(): Int =
        parseGamesOne().sum().also(::println)

    fun starTwo(): Int =
        parseGamesTwo().sum().also(::println)

    private fun parseGamesOne() = input.lines().map { line ->
        val limits = mapOf("red" to 12, "green" to 13, "blue" to 14)

        line.dropPrefix().split(";").all { game ->
            game.split(",").all { box ->
                box.trim().split(" ").let { (num, color) ->
                    limits[color]!! >= num.toInt()
                }
            }
        }.let { valid -> if (valid) line.id().toInt() else 0 }
    }

    private fun parseGamesTwo() = input.lines().map { line ->
        val limits = mutableMapOf("red" to 0, "green" to 0, "blue" to 0)

        line.dropPrefix().split(";").forEach { game ->
            game.split(",").forEach { box ->
                box.trim().split(" ").let { (num, color) ->
                    limits[color] = max(limits[color]!!, num.toInt())
                }
            }
        }

        limits.values.fold(1) { acc, it -> acc * it }
    }

    private fun String.id() = drop(5).takeWhile { it != ':' }
    private fun String.dropPrefix() = drop(5).dropWhile { it != ':' }.drop(1)
}
