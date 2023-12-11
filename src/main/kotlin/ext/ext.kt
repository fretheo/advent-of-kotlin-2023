package ext

import java.util.Scanner

// ------------------------------------------------------------------------------------------------
// Parsing
// ------------------------------------------------------------------------------------------------

fun Scanner.lines() = sequence {
    while (hasNext()) yield(nextLine())
}

fun Scanner.asList() = lines().toList()

// ------------------------------------------------------------------------------------------------
// Grids
// ------------------------------------------------------------------------------------------------

typealias Point = Pair<Int, Int>

val Point.x get() = first
val Point.y get() = second

operator fun List<List<Char>>.get(x: Int, y: Int) = this[y][x]
operator fun List<MutableList<Char>>.set(x: Int, y: Int, value: Char) {
    this[y][x] = value
}

val <T> List<List<T>>.xIndices get() = require(isNotEmpty()).let { first().indices }
val <T> List<List<T>>.yIndices get() = require(isNotEmpty()).let { indices }

inline fun <T> List<List<T>>.gridForEach(action: (x: Int, y: Int, T) -> Unit) {
    for (y in this.indices)
        for (x in this[y].indices)
            action(x, y, this[y][x])
}

inline fun <T, R> List<List<T>>.gridFold(initial: R, operation: (acc: R, x: Int, y: Int, T) -> R): R {
    var accumulator = initial
    for (y in this.indices)
        for (x in this[y].indices)
            accumulator = operation(accumulator, x, y, this[y][x])
    return accumulator
}

val <T> List<List<T>>.gridIndices: List<Pair<Int, Int>>
    get() = this.indices.flatMap { y ->
        this[y].indices.map { x -> x to y }
    }

fun <T> List<List<T>>.replaceWith(value: T) =
    List(size) { (1..first().size).map { value }.toMutableList() }

inline fun <T> List<List<T>>.allInRow(index: Int, predicate: (T) -> Boolean): Boolean =
    index in indices && get(index).all(predicate)

inline fun <T> List<List<T>>.allInCol(columnIndex: Int, predicate: (T) -> Boolean): Boolean =
    isNotEmpty() && columnIndex in first().indices && all { row -> predicate(row[columnIndex]) }

// ------------------------------------------------------------------------------------------------
// Lists
// ------------------------------------------------------------------------------------------------

fun <T> List<T>.collectPairs(predicate: (Pair<T, T>) -> Boolean): Set<Pair<T, T>> {
    val pairs = mutableSetOf<Pair<T, T>>()
    for (i in indices)
        for (j in i + 1 until size)
            (this[i] to this[j])
                .takeIf(predicate)
                ?.let(pairs::add)
    return pairs
}

// ------------------------------------------------------------------------------------------------
// Lists
// ------------------------------------------------------------------------------------------------

fun beautify(c: Char) = when (c) {
    'F'  -> '╔'
    '7'  -> '╗'
    'L'  -> '╚'
    'J'  -> '╝'
    '|'  -> '║'
    '-'  -> '═'
    else -> c
}

fun beautify(grid: List<List<Char>>): String = grid
    .map { it.map(::beautify) }
    .joinToString("\n") { it.joinToString("") }
