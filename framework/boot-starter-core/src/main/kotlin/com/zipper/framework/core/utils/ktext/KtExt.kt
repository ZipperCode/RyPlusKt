package com.zipper.framework.core.utils.ktext

@Suppress("UNCHECKED_CAST")
fun <T> Class<*>.forceCastClass(): Class<T> {
    return this as Class<T>
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.forceCast(): T {
    return this as T
}

@Suppress("UNCHECKED_CAST")
fun <T> Any?.forceCastOrNull(): T? {
    return this as? T?
}

inline fun <reified T> Any?.withType(block: T.() -> Unit) {
    if (this is T) {
        block()
    }
}