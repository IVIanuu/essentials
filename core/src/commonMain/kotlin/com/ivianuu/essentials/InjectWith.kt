package com.ivianuu.essentials

import com.ivianuu.injekt.Inject

inline fun <A, T> injectWith(a: A, @Inject x: (A) -> T): T = x(a)

inline fun <A, B, T> injectWith(a: A, b: B, @Inject x: (A, B) -> T): T = x(a, b)

inline fun <A, B, C, T> injectWith(a: A, b: B, c: C, @Inject x: (A, B, C) -> T): T = x(a, b, c)

inline fun <A, B, C, D, T> injectWith(a: A, b: B, c: C, d: D, @Inject x: (A, B, C, D) -> T): T =
  x(a, b, c, d)

inline fun <A, B, C, D, E, T> injectWith(
  a: A,
  b: B,
  c: C,
  d: D,
  e: E,
  @Inject x: (A, B, C, D, E) -> T
): T = x(a, b, c, d, e)
