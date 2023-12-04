import java.util.Scanner

fun Scanner.lines() = sequence {
    while (hasNext()) yield(nextLine())
}

fun Scanner.asList() = lines().toList()
