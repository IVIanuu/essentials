package com.ivianuu.essentials

context((A) -> T) inline fun <A, T> contextWith(a: A): T = invoke(a)

context((A, B) -> T) inline fun <A, B, T> contextWith(a: A, b: B): T = invoke(a, b)

context((A, B, C) -> T) inline fun <A, B, C, T> contextWith(a: A, b: B, c: C): T = invoke(a, b, c)

context((A, B, C, D) -> T)  inline fun <A, B, C, D, T> contextWith(a: A, b: B, c: C, d: D): T =
  invoke(a, b, c, d)

context((A, B, C, D, E) -> T) inline fun <A, B, C, D, E, T> contextWith(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E
): T = invoke(a, b, c, d, e)
