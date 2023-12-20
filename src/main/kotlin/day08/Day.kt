package day08

import ext.lcm
import java.util.Scanner
import ext.lines

private typealias Network = Map<String, NetworkNode>

class Day(val input: Scanner) {

    fun starOne() = traverse(
        startFrom = { it == "AAA" },
        endAt = { it == "ZZZ" }
    )

    fun starTwo() = traverse(
        startFrom = { it.last() == 'A' },
        endAt = { it.last() == 'Z' }
    )

    // ------------------------------------------------------------------------------------------------
    // Helpers - Processing
    // ------------------------------------------------------------------------------------------------

    private fun traverse(startFrom: (String) -> Boolean, endAt: (String) -> Boolean) = input
        // parse commands and network
        .lines().run { first() to networkFrom(drop(1)) }

        // count how many steps is needed from each starting point
        .let { (commands, network) ->
            with(network) {
                filterKeys(startFrom).map { (nodeId, _) ->
                    countStepsFor(nodeId, commands, endAt)
                }
            }
        }

        // calculating LCM - minimal needed steps
        .reduce(::lcm)

    // ------------------------------------------------------------------------------------------------
    // Helpers - Counting
    // ------------------------------------------------------------------------------------------------

    private fun Network.countStepsFor(startingId: String, commands: String, until: (String) -> Boolean) =
        commands.asRepeatingSequence()
            .fold(get(startingId)!! to 0L) { (node, count), cmd ->
                when {
                    until(node.id) -> return count
                    cmd == 'L'     -> get(node.left)!!
                    else           -> get(node.right)!!
                } to count + 1
            }.second // this should never happen

    private fun String.asRepeatingSequence() =
        sequence { while (true) yieldAll(asSequence()) }

    // ------------------------------------------------------------------------------------------------
    // Helpers - Parsing
    // ------------------------------------------------------------------------------------------------

    private fun networkFrom(lines: Sequence<String>) =
        lines.map(::networkNodeFrom).associateBy { it.id }

    private fun networkNodeFrom(line: String) = line.drop(7).dropLast(1).run {
        NetworkNode(
            id = line.take(3),
            left = take(3),
            right = takeLast(3)
        )
    }
}

private class NetworkNode(val id: String, val left: String, val right: String)
