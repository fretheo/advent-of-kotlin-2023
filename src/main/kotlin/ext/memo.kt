package ext

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private class Memo1<T1, R>(
    private val function: (T1) -> R
) : ReadOnlyProperty<Any?, (T1) -> R>,
    MutableMap<T1, R> by mutableMapOf() {

    override fun getValue(thisRef: Any?, property: KProperty<*>): (T1) -> R =
        { a -> getOrPut(a) { function(a) } }
}

private class Memo2<T1, T2, R>(
    private val function: (T1, T2) -> R
) : ReadOnlyProperty<Any?, (T1, T2) -> R>,
    MutableMap<Pair<T1, T2>, R> by mutableMapOf() {

    override fun getValue(thisRef: Any?, property: KProperty<*>): (T1, T2) -> R {
        return { a, b -> getOrPut(a to b) { function(a, b) } }
    }
}

private class Memo3<T1, T2, T3, R>(
    private val function: (T1, T2, T3) -> R
) : ReadOnlyProperty<Any?, (T1, T2, T3) -> R>,
    MutableMap<Triple<T1, T2, T3>, R> by mutableMapOf() {

    override fun getValue(thisRef: Any?, property: KProperty<*>): (T1, T2, T3) -> R {
        return { a, b, c -> getOrPut(Triple(a, b, c)) { function(a, b, c) } }
    }
}

fun <T1, R> memoize(function: (T1) -> R): ReadOnlyProperty<Any?, (T1) -> R> = Memo1(function)
fun <T1, T2, R> memoize(function: (T1, T2) -> R): ReadOnlyProperty<Any?, (T1, T2) -> R> = Memo2(function)
fun <T1, T2, T3, R> memoize(function: (T1, T2, T3) -> R): ReadOnlyProperty<Any?, (T1, T2, T3) -> R> = Memo3(function)
