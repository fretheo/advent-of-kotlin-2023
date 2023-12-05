package day05

import java.util.Scanner
import lines

class Day(val input: Scanner) {
    fun starOne() = input.parseSeeder()
        .run { findBestLocation(starOneSeedRanges) }
        .also(::println)

    fun starTwo() = input.parseSeeder()
        .run { findBestLocation(starTwoSeedRanges) }
        .also(::println)
}

// ------------------------------------------------------------------------------------------------
// Helpers - Types
// ------------------------------------------------------------------------------------------------

private class Seeder(rawSeeds: List<Long>, val mappers: List<RangeMapper>) {
    val starOneSeedRanges = rawSeeds.map { it..it }
    val starTwoSeedRanges = rawSeeds.chunked(2)
        .map { (start, length) -> start..start + length }
}

private typealias RangeMapper = List<Pair<LongRange, LongRange>>

// ------------------------------------------------------------------------------------------------
// Helpers - Input
// ------------------------------------------------------------------------------------------------

private fun Scanner.parseSeeder() = with(lines().iterator()) {
    val rawSeeds = next()
        .split(":").last().trim()
        .split(" ").map(kotlin.String::toLong)

    next() // empty

    // S2S -> S2F -> F2W -> W2L -> L2T -> T2H -> H2L
    Seeder(rawSeeds, (1..7).map { nextMapper() })
}

private fun Iterator<String>.nextMapper(): RangeMapper {
    next() // ignore header
    return asSequence().takeWhile(String::isNotEmpty).processMapping()
}

private fun Sequence<String>.processMapping(): RangeMapper = fold(mutableListOf()) { acc, mapping ->
    mapping.split(" ").map(String::toLong)
        .let { (t, f, n) -> acc.add(f..<(f + n) to t..<(t + n)) }
        .run { acc }
}

// ------------------------------------------------------------------------------------------------
// Helpers - Processing
// ------------------------------------------------------------------------------------------------

private fun Seeder.findBestLocation(seeds: List<LongRange>) =
    seeds.flatMap(::processSeedRange).minOf { it.first }

private fun Seeder.processSeedRange(seeds: LongRange) =
    mappers.fold(listOf(seeds)) { seed, mapper -> mapper.processSeedRanges(seed) }

private fun RangeMapper.processSeedRanges(seeds: List<LongRange>) =
    seeds.fold(mutableSetOf<LongRange>()) { acc, range ->
        acc.also {
            acc.addAll(mapNotNull { pipe(it.first, it.second, range) })
            acc.addAll(range.splitBy(map { it.first }))
        }
    }.toList()

private fun pipe(src: LongRange, dst: LongRange, input: LongRange): LongRange? {
    val (a, b) = src.first to src.last
    val (x, y) = input.first to input.last

    val left = when {
        a in x..y -> 0
        x in a..b -> x - a
        else      -> return null
    }

    val right = when {
        b in x..y -> 0
        y in a..b -> b - y
        else      -> return null
    }

    return (dst.first + left)..(dst.last - right)
}

private fun LongRange.splitBy(ranges: List<LongRange>): List<LongRange> = ranges
    .filter { first <= it.last && it.first <= last }    // find overlaps
    .sortedBy { it.first }                              // align ranges
    .fold(first to mutableListOf<LongRange>()) {
        (curr, acc), range -> 1 + range.last to acc.apply { add(curr..<range.first) }
    }                                                   // collect split ranges
    .also { it.second.add(it.first..last) }             // add remaining values
    .second.filterNot { it.last < it.first }            // filter out invalid ranges

