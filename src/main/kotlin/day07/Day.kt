package day07

import java.util.Scanner
import lines

class Day(val input: Scanner) {
    fun starOne(): Long = Rules(enableJoker = false).play()
    fun starTwo(): Long = Rules(enableJoker = true).play()

    private fun Rules.play() = input.lines()

        // parse hands
        .map { it.split(" ") }
        .map { (cards, bid) -> Triple(cards, bid, rank(cards)) }

        // play hands
        .sortedWith { (A, _, rankA), (B, _, rankB) ->
            (rankA - rankB).takeIf { it != 0 }
                ?: A.zip(B)
                    .first { it.first != it.second }
                    .let { (a, b) -> orderOf(a) - orderOf(b) }
        }

        // count the score
        .mapIndexed { idx, it -> (idx + 1) * it.second.toLong() }
        .sum()
}

private class Rules(private val enableJoker: Boolean) {

    // ------------------------------------------------------------------------------------------------
    // Helpers - Ranking
    // ------------------------------------------------------------------------------------------------

    fun orderOf(c: Char) = when (enableJoker) {
        true -> "J23456789TQKA" // **
        else -> "23456789TJQKA" // *
    }.indexOf(c)

    fun rank(s: String) = when {
        s.isFive()     -> 7
        s.isFour()     -> 6
        s.isFull()     -> 5
        s.isThree()    -> 4
        s.isTwoPairs() -> 3
        s.isTwo()      -> 2
        s.isOne()      -> 1
        else           -> TODO("No!")
    }

    // ------------------------------------------------------------------------------------------------
    // Helpers - Hand Types
    // ------------------------------------------------------------------------------------------------

    private fun String.isFive() =
        all { it.isJoker() } || removeWildcards().let { maxCharCount(it) == 5 }

    private fun String.isFour() =
        removeWildcards().let { maxCharCount(it) == 4 }

    private fun String.isFull() = removeWildcards()
        .map { it to charCount(it) }.distinct()
        .run { size == 2 && all { it.second in 2..3 } }

    private fun String.isThree() =
        removeWildcards().let { it.hasUniqueChars(3) && maxCharCount(it) == 3 }

    private fun String.isTwoPairs() =
        removeWildcards().let { it.hasUniqueChars(3) && maxCharCount(it) == 2 }

    private fun String.isTwo() =
        removeWildcards().let { it.hasUniqueChars(4) && maxCharCount(it) == 2 }

    private fun String.isOne() =
        removeWildcards().hasUniqueChars(5)

    // ------------------------------------------------------------------------------------------------
    // Helpers - Counters
    // ------------------------------------------------------------------------------------------------

    private fun String.removeWildcards() = filterNot { enableJoker && it.isJoker() }
    private fun Char.isJoker() = equals('J')
    private fun String.charCount(c: Char) = count { it == c } + count { enableJoker && it == 'J' }
    private fun String.maxCharCount(chars: String) = chars.maxOf { charCount(it) }
    private fun String.hasUniqueChars(count: Int) = toSet().size == count
}
