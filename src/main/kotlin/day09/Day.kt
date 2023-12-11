package day09

import java.util.Scanner
import ext.lines

class Day(val input: Scanner) {
    fun starOne() = predictNumber { prev, comp -> comp.last() + prev }
    fun starTwo() = predictNumber { prev, comp -> comp.first() - prev }

    private fun predictNumber(block: (Int, List<Int>) -> Int) = input.lines()
        .map { str -> str.split(" ").map(String::toInt) }
        .sumOf { it.calculateCompression().toList().asReversed().fold(0, block) }

    private fun List<Int>.compressed() =
        windowed(2).map { (a, b) -> b - a }

    private fun List<Int>.calculateCompression() = generateSequence(this) {
        it.compressed().takeUnless { comp -> comp.all(0::equals) }
    }
}
