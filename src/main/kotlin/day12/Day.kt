package day12

import ext.*
import java.util.Scanner

class Day(val input: Scanner) {
    fun starOne() = parseInput(unfold = 1).processRecords()
    fun starTwo() = parseInput(unfold = 5).processRecords()

    private fun parseInput(unfold: Int) = input.lines()
        .map { line -> line.split(" ") }
        .map { (a, b) -> Array(unfold) { a } to Array(unfold) { b } }
        .map { (a, b) -> a.joinToString("?") to b.joinToString(",") }

    private fun Sequence<Pair<String, String>>.processRecords() = this
        .map { (a, b) -> a to b.split(",").map { it.toInt() } }
        .sumOf { comboLine(it.first, it.second) }

    // ------------------------------------------------------------------------------------------------
    // Helpers - Processing
    // ------------------------------------------------------------------------------------------------

    private val comboLine: (String, List<Int>) -> Long by memoize { line, sizes ->
        when {
            sizes.isEmpty() -> if (line.all { it in ".?" }) 1L else 0L
            line.isEmpty()  -> 0L
            else            -> when (line.first()) {
                '#'  -> comboHash(line, sizes)                                  // [ # ] - parse hash group
                '?'  -> comboHash(line, sizes) + comboLine(line.drop(1), sizes) // [ ? ] - check both branches
                else -> comboLine(line.dropWhile('.'::equals), sizes)           // [ . ] - look for another character
            }
        }
    }

    private val comboHash: (String, List<Int>) -> Long by memoize { line, sizes ->
        val size = sizes.first()
        when {
            size > line.length                  -> 0L // not enough characters
            line.take(size).any('.'::equals)    -> 0L // group contains a break
            line.getOrElse(size) { '.' } == '#' -> 0L // no break after the group
            else                                -> comboLine(line.drop(size + 1), sizes.drop(1))
        }
    }
}
