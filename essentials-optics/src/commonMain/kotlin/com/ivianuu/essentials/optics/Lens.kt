package com.ivianuu.essentials.optics

interface Lens<T, V> {
  fun get(t: T): V
  fun set(t: T, v: V): T
}

inline fun <T, V> Lens(
  crossinline get: (T) -> V,
  crossinline set: (T, V) -> T
): Lens<T, V> = object : Lens<T, V> {
  override fun get(t: T): V = get.invoke(t)
  override fun set(t: T, v: V): T = set.invoke(t, v)
}

inline fun <T, V> Lens<T, V>.update(t: T, transform: V.() -> V): T = set(t, transform(get(t)))

operator fun <A, B, C> Lens<A, B>.plus(other: Lens<B, C>): Lens<A, C> = Lens(
  { a -> other.get(get(a)) },
  { b, c -> set(b, other.set(get(b), c)) }
)
