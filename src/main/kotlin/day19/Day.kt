package day19

import ext.*
import java.util.Scanner

class Day(val input: Scanner) {
    private val accepted = input.lines().takeWhile(String::isNotBlank)
        .associate(::parseWorkflow).let(::WorkflowProcessor)
        .executeFor(Part(1..4000, 1..4000, 1..4000, 1..4000))

    private val parts = input.lines().map(::parsePart)

    fun starOne() = parts
        .filter { part -> accepted.any { part in it } }
        .sumOf(List<Int>::sum)

    fun starTwo() = accepted.sumOf(Part::count)
}

// ------------------------------------------------------------------------------------------------
// Helper: Processing
// ------------------------------------------------------------------------------------------------

private class WorkflowProcessor(val workflows: Map<String, List<Next>>) {
    private val queue = mutableListOf<Pair<Next, Part>>()
    private val accepted = mutableListOf<Part>()

    fun executeFor(start: Part): List<Part> {
        queue.clear()
        accepted.clear()

        queue += NextWorkflow("in") as Next to start

        while (queue.isNotEmpty()) {
            val (next, part) = queue.removeFirst()
            when (next) {
                is NextReject   -> continue
                is NextAccept   -> accepted += part
                is NextWorkflow -> applyRules(part, next.id).forEach(queue::add)

                is NextIf       -> TODO()
            }
        }

        return accepted
    }

    private fun applyRules(part: Part, workflowId: String): List<Pair<Next, Part>> {
        val workflow = workflows[workflowId] ?: return emptyList()
        var currentPart = part
        val mp = workflow.map { rule ->
            when (rule) {
                is NextIf -> {
                    val (current, setter) = when (rule.variable) {
                        "x"  -> currentPart.x to { new: IntRange -> currentPart.copy(x = new) }
                        "m"  -> currentPart.m to { new: IntRange -> currentPart.copy(m = new) }
                        "a"  -> currentPart.a to { new: IntRange -> currentPart.copy(a = new) }
                        "s"  -> currentPart.s to { new: IntRange -> currentPart.copy(s = new) }
                        else -> TODO()
                    }
                    val result = current.splitBy(rule)

                    currentPart = setter(result.last())
                    rule.next to setter(result.first())
                }

                else      -> rule to currentPart
            }
        }

        return mp
    }

    private fun IntRange.splitBy(rule: NextIf) = when (rule.comparison) {
        '<'  -> listOf(first..(rule.value - 1), (rule.value)..last)
        else -> listOf((rule.value + 1)..last, first..(rule.value))
    }
}

// ------------------------------------------------------------------------------------------------
// Helper: Parsing
// ------------------------------------------------------------------------------------------------

private fun parsePart(part: String) = regexPart(part)!!.map(String::toInt)
private fun parseWorkflow(wf: String): Pair<String, List<Next>> =
    regexWorkflow(wf)!!.let { (id, rules) ->
        id to rules.split(',').map {
            val rule = it.split(":")
            val next = when (rule.last()) {
                "A"  -> NextAccept
                "R"  -> NextReject
                else -> NextWorkflow(rule.last())
            }

            regexRule(rule.first())
                ?.let { (name, sign, value) ->
                    NextIf(name, sign.first(), value.toInt(), next)
                } ?: next
        }
    }

private fun regexPart(part: String) =
    Regex("""\{x=(\d+),m=(\d+),a=(\d+),s=(\d+)\}""").find(part)?.groupValues?.takeLast(4)

private fun regexWorkflow(workflow: String) =
    Regex("""(.+)\{(.*)\}""").find(workflow)?.groupValues?.takeLast(2)

private fun regexRule(rule: String) =
    Regex("""(\w+)(.)(\d+)""").find(rule)?.groupValues?.takeLast(3)

// ------------------------------------------------------------------------------------------------
// Helper: Structs
// ------------------------------------------------------------------------------------------------

private data class Part(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange) {
    val count = x.length * m.length * a.length * s.length

    operator fun contains(input: List<Int>): Boolean = input
        .let { (g, i, f, t) -> g in x && i in m && f in a && t in s }
}

private data object NextAccept : Next
private data object NextReject : Next
private class NextWorkflow(val id: String) : Next
private class NextIf(val variable: String, val comparison: Char, val value: Int, next: Next) : Next by next
private sealed interface Next {
    val next get() = this
}
