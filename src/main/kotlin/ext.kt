import java.util.Scanner

fun Scanner.lines() = sequence {
    while (hasNext()) yield(nextLine())
}
