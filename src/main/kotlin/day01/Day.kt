package day01

import java.util.Scanner
import ext.lines

class Day(val input: Scanner) {
    fun starOne() = inputNums(named = false).sum()
    fun starTwo() = inputNums(named = true).sum()

    private fun inputNums(named: Boolean) = input.lines()
        .map { it.takeUnless { named } ?: it.augmented() }
        .map { "${it.first(Char::isDigit)}${it.last(Char::isDigit)}".toInt() }

    private fun String.augmented() =
        listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
            .foldIndexed(this) { idx, acc, it -> acc.replace(it, "$it${idx + 1}$it") }
}
