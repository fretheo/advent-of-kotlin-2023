package day06

import asList
import java.util.Scanner

class Day(val input: Scanner) {
    fun starOne() = input.asList()
        .run { numbersFrom(first()) zip numbersFrom(last()) }
        .map { wins(it.first, it.second) }
        .reduce(Int::times)

    fun starTwo() = input.asList()
        .run { numberFrom(first()) to numberFrom(last()) }
        .let { wins(it.first, it.second) }

    private fun wins(time: Long, dist: Long) =
        (1 until time).count { (time - it) * it - dist > 0 }

    private fun numbersFrom(line: String) =
        line.split(Regex("""\s+""")).drop(1).map(String::toLong)

    private fun numberFrom(line: String) =
        numbersFrom(line).joinToString("").toLong()
}
