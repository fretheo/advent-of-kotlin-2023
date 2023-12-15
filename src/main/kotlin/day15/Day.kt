package day15

import java.util.Scanner

class Day(val input: Scanner) {
    fun starOne() = parseSequence().sumOf(::hash)

    fun starTwo(): Int = parseSequence()
        .fold(Boxes(256) { mutableListOf() }) { table, x ->
            val chars = x.takeWhile { it !in "=-" }
            val len = x.takeLastWhile { it !in "=-" }
            val op = x.first { it in "=-" }

            val idx = hash(chars)

            when (op) {
                '-' -> table[idx].removeIf { it.first == chars }
                '=' -> table[idx].apply {
                    val pos = indexOfFirst { it.first == chars }
                    if (pos < 0) add(chars to len) else this[pos] = chars to len
                }
            }

            table
        }
        .mapIndexed { idx, it -> idx + 1 to it }
        .filter { (_, lens) -> lens.isNotEmpty() }
        .sumOf { (box, lens) ->
            box * lens.mapIndexed { idx, it -> (idx + 1) * it.second.toInt() }.sum()
        }

    private fun parseSequence() =
        input.nextLine().split(",").asSequence()

    private fun hash(s: String) =
        s.fold(0) { acc, it -> ((acc + it.code) * 17) % 256 }
}

private typealias Boxes = Array<MutableList<Pair<String, String>>>
