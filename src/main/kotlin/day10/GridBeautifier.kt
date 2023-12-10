package day10

object GridBeautifier {
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
}
