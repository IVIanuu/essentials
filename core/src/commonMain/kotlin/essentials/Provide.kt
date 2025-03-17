package essentials

import injekt.*

inline fun <T> implicitly(x: T = inject): T = x

inline fun <A, R> provide(value: A, block: (@Provide A) -> R): R =
  block(value)

inline fun <F> provider(provider: F): @Provide F = provider
