package ext

import java.util.Scanner

// ------------------------------------------------------------------------------------------------
// Parsing
// ------------------------------------------------------------------------------------------------

fun Scanner.lines() = sequence {
    while (hasNext()) yield(nextLine())
}

fun Scanner.asList() = lines().toList()
fun Scanner.asGrid() = asList().map { it.toList() }
fun <T> Scanner.asGridOf(transform: (Char) -> T) = asList().map { it.map(transform) }
fun List<String>.asGrid() = map { it.toList() }
fun Sequence<String>.asGrid() = toList().asGrid()
fun Sequence<String>.asMutableGrid() = toList().map { it.toMutableList() }

// ------------------------------------------------------------------------------------------------
// Grids
// ------------------------------------------------------------------------------------------------

typealias IntPoint = Pair<Int, Int>
typealias LongPoint = Pair<Long, Long>
typealias Grid<T> = List<List<T>>
typealias MutableGrid<T> = List<MutableList<T>>

val IntPoint.x get() = first
val IntPoint.y get() = second
val LongPoint.x get() = first
val LongPoint.y get() = second

operator fun <T> List<List<T>>.get(x: Int, y: Int) = this[y][x]
operator fun <T> List<MutableList<T>>.set(x: Int, y: Int, value: T) {
    this[y][x] = value
}

val <T> Grid<T>.xIndices get() = require(isNotEmpty()).let { first().indices }
val <T> Grid<T>.yIndices get() = require(isNotEmpty()).let { indices }

val <T> Grid<T>.X get() = xIndices.last
val <T> Grid<T>.Y get() = yIndices.last

inline fun <T> Grid<T>.gridForEach(action: (x: Int, y: Int, T) -> Unit) {
    for (y in this.indices)
        for (x in this[y].indices)
            action(x, y, this[y][x])
}

inline fun <T, R> Grid<T>.foldColRow(initial: R, operation: (acc: R, x: Int, y: Int, T) -> R): R {
    var accumulator = initial
    for (x in xIndices) for (y in yIndices)
        accumulator = operation(accumulator, x, y, this[y][x])
    return accumulator
}

inline fun <T, R> Grid<T>.foldRowCol(initial: R, operation: (acc: R, x: Int, y: Int, T) -> R): R {
    var accumulator = initial
    for (y in yIndices) for (x in xIndices)
        accumulator = operation(accumulator, x, y, this[y][x])
    return accumulator
}

val <T> Grid<T>.gridIndices: List<Pair<Int, Int>>
    get() = this.indices.flatMap { y ->
        this[y].indices.map { x -> x to y }
    }

fun <T> Grid<T>.positionOfFirst(predicate: (T) -> Boolean): Pair<Int, Int> {
    for (y in indices) this[y]
        .indexOfFirst(predicate)
        .takeUnless { it < 0 }
        ?.let { x -> return x to y }
    throw IllegalStateException()
}

fun <T> Grid<T>.replaceWith(value: T) =
    List(size) { (1..first().size).map { value }.toMutableList() }

inline fun <T> Grid<T>.allInRow(index: Int, predicate: (T) -> Boolean): Boolean =
    index in indices && get(index).all(predicate)

inline fun <T> Grid<T>.allInCol(columnIndex: Int, predicate: (T) -> Boolean): Boolean =
    isNotEmpty() && columnIndex in first().indices && all { row -> predicate(row[columnIndex]) }

fun <T> Grid<T>.transposed(): Grid<T> =
    List(X + 1) { x -> List(Y + 1) { y -> this[y][x] } }

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
// Ranges
// ------------------------------------------------------------------------------------------------

val IntRange.length get() = (1L + last - first)

// ------------------------------------------------------------------------------------------------
// LCM / GCD
// ------------------------------------------------------------------------------------------------

fun lcm(a: Long, b: Long): Long =
    a / gcd(a, b) * b

tailrec fun gcd(a: Long, b: Long): Long =
    if (b == 0L) a else gcd(b, a % b)

// ------------------------------------------------------------------------------------------------
// Misc
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

fun beautify(grid: Grid<Char>): String = grid
    .map { it.map(::beautify) }
    .joinToString("\n") { it.joinToString("") }

fun Grid<Char>.printGrid() =
    joinToString("\n") { it.joinToString("", postfix = "\n") }.let(::println)

enum class Direction { U, L, D, R }
