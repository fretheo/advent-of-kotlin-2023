import java.util.Scanner

fun inputScanner(day: Int) = ClassLoader
    .getSystemResourceAsStream("Day${"$day".padStart(2, '0')}.txt")
    .let { requireNotNull(it) { "Cannot read from $it" } }
    .let { Scanner(it) }
