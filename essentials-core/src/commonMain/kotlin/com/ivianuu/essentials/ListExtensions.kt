package com.ivianuu.essentials

inline fun <T> Iterable<T>.dropAllAfter(predicate: (T) -> Boolean) = buildList {
  for (item in this@dropAllAfter) {
    add(item)
    if (predicate(item)) break
  }
}

inline fun <T> Iterable<T>.takeAllBefore(predicate: (T) -> Boolean) = buildList {
  for (item in this@takeAllBefore) {
    if (predicate(item)) break
    add(item)
  }
}
