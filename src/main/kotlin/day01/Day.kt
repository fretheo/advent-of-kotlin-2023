package day01

import java.util.Scanner
import lines

class Day(val input: Scanner) {
    fun starOne() = inputNums(named = false).sum().also(::println)
    fun starTwo() = inputNums(named = true).sum().also(::println)

    private fun inputNums(named: Boolean) = input.lines().map { line ->
        (if (named) line.augmented() else line)
            .run { get(indexOfFirst(Char::isDigit)) to get(indexOfLast(Char::isDigit)) }
            .let { (a, b) -> "$a$b".toInt() }
    }

    private fun String.augmented() =
        listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
            .foldIndexed(this) { idx, acc, it -> acc.replace(it, "$it${idx + 1}$it") }
}
