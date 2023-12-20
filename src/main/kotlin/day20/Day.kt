package day20

import day20.Pulse.*
import ext.*
import java.util.Scanner

class Day(val input: Scanner) {
    fun starOne(): Long {
        var (ls, hs, pressed) = arrayOf(0L, 0L, 0L)

        input.lines().parseModules().pressButton(
            repeatWhile = { pressed++ < 1000 },
            action = { module, pulse ->
                if (pulse == Low) ls += module.dst.size
                if (pulse == High) hs += module.dst.size
            }
        )

        return ls * hs
    }

    fun starTwo(): Long {
        val modules = input.lines().parseModules()

        val finalModule = modules.values.first { "rx" in it.dst } as Conjunction
        val finalInputCycles = mutableMapOf<String, Long>()

        var pressed = 0L

        modules.pressButton(
            repeatWhile = {
                ++pressed
                finalInputCycles.size != finalModule.sources.size
            },
            action = { module, _ ->
                if (module !== finalModule) return@pressButton
                module.sources
                    .filter { it.key !in finalInputCycles }
                    .filter { it.value == High }
                    .forEach { finalInputCycles[it.key] = pressed }
            }
        )

        return finalInputCycles.values.reduce(::lcm)
    }
}

// ------------------------------------------------------------------------------------------------
// Helper: Processing
// ------------------------------------------------------------------------------------------------

private fun Map<String, Module>.pressButton(repeatWhile: () -> Boolean, action: (Module, Pulse) -> Unit) {
    val queue = mutableListOf<Signal>()

    while (repeatWhile()) {
        queue.add(Signal("button", Low, "broadcaster"))
        action(Broadcaster(listOf("broadcaster")), Low) // button press

        while (queue.isNotEmpty()) {
            val (src, pulse, dst) = queue.removeFirst()
            val module = this[dst] ?: continue
            val outgoing = when (module) {
                is Broadcaster -> pulse
                is FlipFlop    -> module.pipe(pulse)
                is Conjunction -> module.pipe(src, pulse)
            } ?: continue

            module.dst
                .map { Signal(module.id, outgoing, it) }
                .forEach(queue::add)

            action(module, outgoing)
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Helper: Parsing
// ------------------------------------------------------------------------------------------------

private fun Sequence<String>.parseModules() = this
    .map { it.split(Regex(""" -> """)) }
    .map { (src, dst) -> src to dst.split(',').map(String::trim) }
    .map { (src, dst) -> parseModule(src, dst) }
    .associateBy { it.id }
    .also { modules ->
        // populate conjunction sources
        for (conjunction in modules.filterValues { it is Conjunction })
            for (module in modules.filterValues { conjunction.key in it.dst }.values)
                (conjunction.value as Conjunction).sources[module.id] = Low
    }

private fun parseModule(src: String, dst: List<String>) = when {
    src.startsWith("%") -> FlipFlop(src.drop(1), dst)
    src.startsWith("&") -> Conjunction(src.drop(1), dst)
    else                -> Broadcaster(dst)
}

// ------------------------------------------------------------------------------------------------
// Helper: Structs
// ------------------------------------------------------------------------------------------------

private enum class Pulse { Low, High }
private data class Signal(val src: String, val pulse: Pulse, val dst: String)

private sealed interface Module {
    val id: String
    val dst: List<String>
}

private class Broadcaster(override val dst: List<String>) : Module {
    override val id: String = "broadcaster"
}

private class FlipFlop(override val id: String, override val dst: List<String>) : Module {

    private var enabled = false
    fun pipe(pulse: Pulse) = when (pulse) {
        High -> null
        Low  -> enabled.not()
            .also { enabled = it }
            .let { if (it) High else Low }
    }
}

private class Conjunction(override val id: String, override val dst: List<String>) : Module {

    val sources = mutableMapOf<String, Pulse>()
    fun pipe(src: String, pulse: Pulse) = apply { sources[src] = pulse }
        .let { if (sources.values.all { it == High }) Low else High }
}
