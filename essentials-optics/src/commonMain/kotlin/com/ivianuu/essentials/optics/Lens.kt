package com.ivianuu.essentials.optics

interface Lens<T, V> {
    fun get(t: T): V
    fun set(t: T, v: V): T
}

fun <T, V> Lens(get: (T) -> V, set: (T, V) -> T): Lens<T, V> = object : Lens<T, V> {
    override fun get(t: T): V = get.invoke(t)
    override fun set(t: T, v: V): T = set.invoke(t, v)
}
